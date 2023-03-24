package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 订单接口实现类
 * @date 2022/10/25 11:42
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;

    @Resource
    XcOrdersMapper ordersMapper;

    @Resource
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Resource
    XcPayRecordMapper payRecordMapper;


    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //创建商品订单
        XcOrders orders = saveXcOrders(userId, addOrderDto);
        //添加支付记录
        XcPayRecord payRecord = createPayRecord(orders);

        //生成支付二维码
        String qrCode = null;
        try {
            //url要可以被模拟器访问到，url为下单接口(稍后定义)
            qrCode = new QRCodeUtil().createQRCode("http://192.168.101.1/api/orders/requestpay?payNo="+payRecord.getPayNo(), 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码出错");
        }
        //封装要返回的数据
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        //支付二维码
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        LambdaQueryWrapper<XcPayRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcPayRecord::getPayNo, payNo);
        return payRecordMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        //支付结果
        String trade_status = payStatusDto.getTrade_status();

        if ("TRADE_SUCCESS".equals(trade_status)) {
            //支付流水号
            String payNo = payStatusDto.getOut_trade_no();
            //查询支付流水
            XcPayRecord payRecord = getPayRecordByPayNo(payNo);

            //支付金额变为分
            BigDecimal totalPrice = BigDecimal.valueOf(payRecord.getTotalPrice());
            BigDecimal total_amount = new BigDecimal(payStatusDto.getTotal_amount());
            //校验是否一致
            if (payStatusDto.getApp_id().equals(APP_ID) && totalPrice.compareTo(total_amount) == 0) {
                String status = payRecord.getStatus();
                if ("601001".equals(status)) {//未支付时进行处理

                    log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
                    XcPayRecord payRecord_u = new XcPayRecord();
                    payRecord_u.setStatus("601002");//支付成功
                    payRecord_u.setOutPayChannel("Alipay");
                    payRecord_u.setOutPayNo(payStatusDto.getTrade_no());//支付宝交易号
                    payRecord_u.setPaySuccessTime(LocalDateTime.now());//通知时间
                    int update1 = payRecordMapper.update(payRecord_u, new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));

                    if (update1 > 0) {
                        log.info("收到支付通知，更新支付交易状态成功.付交易流水号:{},支付结果:{}", payNo, trade_status);

                        // 找到订单表所关联的外部业务系统的主键

                        // 向消息表插入记录，标记为支付结果通知，外部业务主键，订单业务类型

                    } else {
                        log.error("收到支付通知，更新支付交易状态失败.支付交易流水号:{},支付结果:{}", payNo, trade_status);
                    }
                    //关联的订单号
                    Long orderId = payRecord.getOrderId();
                    XcOrders orders = ordersMapper.selectById(orderId);
                    if (orders != null) {
                        XcOrders order_u = new XcOrders();
                        order_u.setStatus("600002");
                        int update = ordersMapper.update(order_u, new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getId, orderId));
                        if (update > 0) {
                            log.info("收到支付通知，更新订单状态成功.付交易流水号:{},支付结果:{},订单号:{},状态:{}", payNo, trade_status, orderId, "600002");
                        } else {
                            log.error("收到支付通知，更新订单状态失败.支付交易流水号:{},支付结果:{},订单号:{},状态:{}", payNo, trade_status, orderId, "600002");
                        }

                    } else {
                        log.error("收到支付通知，根据交易记录找不到订单,交易记录号:{},订单号:{}", payRecord.getPayNo(), orderId);
                    }

                }
            }
        }

    }

    //添加支付记录
    public XcPayRecord createPayRecord(XcOrders orders){
        XcPayRecord payRecord = new XcPayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);//支付记录交易号
        //记录关键订单id
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");//未支付
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;
    }

    //创建商品订单
    @Transactional
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {

        //选课记录id
        String outBusinessId = addOrderDto.getOutBusinessId();
        //对订单插入进行幂等性处理
        //根据选课记录id从数据库查询订单信息
        XcOrders orders = getOrderByBusinessId(outBusinessId);
        if(orders!=null){
            return orders;
        }

        //添加订单
        orders = new XcOrders();
        long orderId = IdWorkerUtils.getInstance().nextId();//订单号
        orders.setId(orderId);
        orders.setTotalPrice(addOrderDto.getTotalPrice());
        orders.setCreateDate(LocalDateTime.now());
        orders.setStatus("600001");//未支付
        orders.setUserId(userId);
        orders.setOrderType(addOrderDto.getOrderType());
        orders.setOrderName(addOrderDto.getOrderName());
        orders.setOrderDetail(addOrderDto.getOrderDetail());
        orders.setOrderDescrip(addOrderDto.getOrderDescrip());
        orders.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        ordersMapper.insert(orders);
        //插入订单明细表
        String orderDetailJson = addOrderDto.getOrderDetail();
        //将json转成List
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        //将明细List插入数据库
        xcOrdersGoods.forEach(ordersGodds->{
            //在明细中记录订单号
            ordersGodds.setOrderId(orderId);
            ordersGoodsMapper.insert(ordersGodds);
        });

        return orders;


    }

    //根据业务id查询订单
    public XcOrders getOrderByBusinessId(String businessId) {
        return ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
    }

}

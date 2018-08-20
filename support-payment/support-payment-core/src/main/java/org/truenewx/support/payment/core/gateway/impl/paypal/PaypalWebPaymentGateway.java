package org.truenewx.support.payment.core.gateway.impl.paypal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.truenewx.core.enums.Program;
import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.PaymentDefinition;
import org.truenewx.support.payment.core.PaymentRequestParameter;
import org.truenewx.support.payment.core.PaymentResult;
import org.truenewx.support.payment.core.gateway.PaymentChannel;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * PayPal 贝宝WEB支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PaypalWebPaymentGateway extends PaypalPaymentGateway {

    public PaypalWebPaymentGateway() {
        setTerminals(new Terminal(null, null, Program.WEB));
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        // 建立金额与币种
        Amount amountDetail = new Amount();
        amountDetail.setCurrency(definition.getCurrency().toString());
        amountDetail.setTotal(definition.getAmount().toString());
        Transaction transaction = new Transaction();
        transaction.setAmount(amountDetail);
        transaction.setInvoiceNumber(definition.getOrderNo());
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        // 建立支付方式
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setNoteToPayer(definition.getDescription());

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(getResultShowUrl());
        redirectUrls.setReturnUrl(getResultConfirmUrl());// 回调路径
        payment.setRedirectUrls(redirectUrls);
        try {
            APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
            Payment createdPayment = payment.create(apiContext);
            PaymentRequestParameter parameter = new PaymentRequestParameter();
            parameter.setSelfTarget(true);
            for (Links links : createdPayment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    parameter.setUrl(links.getHref());
                }
            }
            return parameter;
        } catch (PayPalRESTException e) {
            // 发起支付失败
            this.logger.error(e.toString());
        }
        return null;
    }

    @Override
    public PaymentResult getResult(boolean confirmed, Terminal terminal,
            Map<String, String> params) {
        Payment payment = new Payment();
        payment.setId(params.get("paymentId"));
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(params.get("PayerID"));
        try {
            APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
            Payment executeResult = payment.execute(apiContext, paymentExecute);
            this.logger.info(executeResult.toJSON());
            // TODO 此处应返回结果，后续添加
        } catch (PayPalRESTException e) {
            this.logger.error(e.toString());
        }
        return null;
    }

}

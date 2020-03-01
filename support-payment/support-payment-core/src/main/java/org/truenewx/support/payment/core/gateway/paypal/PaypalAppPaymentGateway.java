package org.truenewx.support.payment.core.gateway.paypal;

import java.math.BigDecimal;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.core.enums.Program;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.util.MathUtil;
import org.truenewx.support.payment.core.PaymentDefinition;
import org.truenewx.support.payment.core.PaymentRequestParameter;
import org.truenewx.support.payment.core.PaymentResult;
import org.truenewx.support.payment.core.gateway.PaymentChannel;

import com.paypal.api.payments.Item;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * PayPal 贝宝APP支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PaypalAppPaymentGateway extends PaypalPaymentGateway {

    public PaypalAppPaymentGateway() {
        setTerminals(new Terminal(null, null, Program.APP));
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        return null;
    }

    @Override
    public PaymentResult getResult(boolean confirmed, Terminal terminal,
            Map<String, String> params) {
        APIContext apiContext = new APIContext(getClientId(), getClientSecret(), getMode());
        String paymentId = params.get("paymentId");
        try {
            Payment result = Payment.get(apiContext, paymentId);
            this.logger.info(result.toJSON());
            if ("approved".equals(result.getState())) {
                Transaction transaction = result.getTransactions().get(0);
                RelatedResources relatedResources = transaction.getRelatedResources().get(0);
                Sale sale = relatedResources.getSale();

                String gatewayPaymentNo = sale.getId();
                BigDecimal amount = MathUtil.parseDecimal(sale.getAmount().getTotal());
                Item item = transaction.getItemList().getItems().get(0);
                String orderNo = item.getSku();

                PaymentResult paymentResult = new PaymentResult(gatewayPaymentNo, amount, terminal,
                        orderNo, Strings.EMPTY);
                return paymentResult;
            }
        } catch (PayPalRESTException e) {
            this.logger.error(e.toString());
        }
        return null;
    }

}

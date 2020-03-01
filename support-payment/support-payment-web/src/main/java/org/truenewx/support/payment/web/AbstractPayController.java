package org.truenewx.support.payment.web;

import com.tenpay.util.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.PaymentManager;
import org.truenewx.support.payment.core.PaymentResult;
import org.truenewx.support.payment.core.gateway.PaymentGateway;

import javax.servlet.http.HttpServletRequest;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 抽象的支付控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractPayController {

    @Autowired
    protected PaymentManager paymentManager;

    public List<PaymentGateway> getGateways(Terminal terminal) {
        return this.paymentManager.getGateways(terminal);
    }

    @RequestMapping(value = "/result/confirm/{gatewayName}")
    @ResponseBody
    public String confirm(@PathVariable("gatewayName")
            String gatewayName, HttpServletRequest request) throws HandleableException {
        Map<String, String> params = getHttpRequestParams(request);
        if (params != null && params.size() > 0) {
            PaymentResult result = this.paymentManager.notifyResult(gatewayName, true, null,
                    params);
            if (result != null) {
                return result.getResponse();
            }
        }
        return null;
    }

    private Map<String, String> getHttpRequestParams(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams != null && requestParams.size() > 0) {
            Map<String, String> params = new HashMap<>();
            for (Entry<String, String[]> entry : requestParams.entrySet()) {
                String name = entry.getKey();
                String[] values = entry.getValue();
                params.put(name, StringUtils.join(values, Strings.COMMA));
            }
            return params;
        } else {
            try {
                Reader reader = request.getReader();
                String xml = IOUtils.toString(reader);
                reader.close();
                return XmlUtil.doXmlParse(xml);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping(value = "/result/show/{gatewayName}/{terminal}")
    public String show(@PathVariable("gatewayName")
            String gatewayName, @PathVariable(value = "terminal", required = false)
            String terminal, HttpServletRequest request, RedirectAttributes attr)
            throws HandleableException {
        Map<String, String> params = getHttpRequestParams(request);
        PaymentResult result = this.paymentManager.notifyResult(gatewayName, false,
                new Terminal(terminal), params);
        return getShowResultName(result);
    }

    protected abstract String getShowResultName(PaymentResult result);

}

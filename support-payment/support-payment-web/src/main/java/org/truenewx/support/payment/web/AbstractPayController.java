package org.truenewx.support.payment.web;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import org.truenewx.support.payment.core.gateway.PaymentResult;

import com.tenpay.util.XmlUtil;

/**
 * 抽象的支付控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RequestMapping("/pay")
public abstract class AbstractPayController {

    @Autowired
    private PaymentManager paymentManager;

    /**
     * 获取支付请求参数集<br/>
     * 子类应实现该方法，并作为RPC方法对外提供
     *
     * @param gatewayName
     *            支付网关名称
     * @param terminal
     *            终端类型
     * @param orderNo
     *            订单编号
     * @return 支付请求参数集
     */
    public abstract Map<String, String> getPayRequestParams(String gatewayName, Terminal terminal,
            String orderNo);

    @RequestMapping(value = "/result/confirm/{gatewayName}")
    @ResponseBody
    public String confirm(@PathVariable("gatewayName") final String gatewayName,
            final HttpServletRequest request) throws HandleableException {
        final Map<String, String> params = getHttpRequestParams(request);
        if (params != null && params.size() > 0) {
            final PaymentResult result = this.paymentManager.notifyResult(gatewayName, true,
                    params);
            if (result != null) {
                return result.getResponse();
            }
        }
        return null;
    }

    private Map<String, String> getHttpRequestParams(final HttpServletRequest request) {
        final Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams != null && requestParams.size() > 0) {
            final Map<String, String> params = new HashMap<>();
            for (final Entry<String, String[]> entry : requestParams.entrySet()) {
                final String name = entry.getKey();
                final String[] values = entry.getValue();
                params.put(name, StringUtils.join(values, Strings.COMMA));
            }
            return params;
        } else {
            try {
                final Reader reader = request.getReader();
                final String xml = IOUtils.toString(reader);
                reader.close();
                return XmlUtil.doXmlParse(xml);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping(value = "/result/show/{gatewayName}")
    public String show(@PathVariable("gatewayName") final String gatewayName,
            final HttpServletRequest request, final RedirectAttributes attr)
            throws HandleableException {
        final Map<String, String> params = getHttpRequestParams(request);
        final PaymentResult result = this.paymentManager.notifyResult(gatewayName, false, params);
        return getShowResultName(result);
    }

    protected abstract String getShowResultName(PaymentResult result);

}

package lithium.service.cashier.processor.interswitch;

import lithium.service.cashier.processor.interswitch.api.schema.CustomerInformationRequest;
import lithium.service.cashier.processor.interswitch.api.schema.Payment;
import lithium.service.cashier.processor.interswitch.api.schema.PaymentNotificationItem;
import lithium.service.cashier.processor.interswitch.api.schema.PaymentNotificationRequest;
import lithium.service.cashier.processor.interswitch.services.CustomerInformationExecutor;
import lithium.service.cashier.processor.interswitch.services.PaymentNotificationExecutor;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PayDirectTest {

    public static final String CUSTOMER_INFO_REQUEST =
            "<CustomerInformationRequest>" +
            "<MerchantReference>6033</MerchantReference>" +
            "<CustReference>2340674303900</CustReference>" +
            "<PaymentItemCode>01</PaymentItemCode>" +
            "<ServiceUsername></ServiceUsername>" +
            "<ServicePassword></ServicePassword>" +
            "<ThirdPartyCode>STB</ThirdPartyCode>" +
            "</CustomerInformationRequest>";
    public static final String PAYMENT_NOTIFICATION_REQUEST =
            "<PaymentNotificationRequest xmlns:ns2='http://techquest.interswitchng.com/' xmlns:ns3='http://www.w3.org/2003/05/soap-envelope'>" +
            "<ServiceUrl>http://localhost:9000/service-cashier-processor-interswitch/public/livescore_nigeria/paydirect</ServiceUrl>" +
            "<ServiceUsername></ServiceUsername>" +
            "<ServicePassword></ServicePassword>" +
            "<FtpUrl></FtpUrl>" +
            "<FtpUsername>" +
            "</FtpUsername>" +
            "<FtpPassword>" +
            "</FtpPassword>" +
            "<Payments>" +
            "<Payment>" +
            "<PaymentLogId>4986856453926</PaymentLogId>" +
            "<CustReference>2340674303900</CustReference>" +
            "<AlternateCustReference></AlternateCustReference>" +
            "<Amount>101.00</Amount>" +
            "<PaymentMethod>Cash</PaymentMethod>" +
            "<PaymentReference>BOND|TEST|BRH|2020-11-10|1230909019</PaymentReference>" +
            "<TerminalId>" +
            "</TerminalId>" +
            "<ChannelName>Bank Branc</ChannelName>" +
            "<Location>" +
            "</Location>" +
            "<PaymentDate>11/12/2014 07:00:36</PaymentDate>" +
            "<InstitutionId>{{InstitutionCode}}</InstitutionId>" +
            "<InstitutionName>TEST MERCHANT</InstitutionName>" +
            "<BranchName>{{BranchName}}</BranchName>" +
            "<BankName>Bond Bank</BankName>" +
            "<CustomerName>Adaobi Igwe</CustomerName>" +
            "<OtherCustomerInfo>08012345678</OtherCustomerInfo>" +
            "<ReceiptNo>23424323</ReceiptNo>" +
            "<CollectionsAccount>2090112022</CollectionsAccount>" +
            "<BankCode>Bond</BankCode>" +
            "<CustomerAddress></CustomerAddress>" +
            "<CustomerPhoneNumber>08012345678</CustomerPhoneNumber>" +
            "<DepositorName>${CustomerName}</DepositorName>" +
            "<DepositSlipNumber>${DepositSlipNo}</DepositSlipNumber>" +
            "<PaymentCurrency>566</PaymentCurrency>" +
            "<PaymentItems>" +
            "<PaymentItem>" +
            "<ItemName>TEST MERCHANT</ItemName>" +
            "<ItemCode>{{ItemCode}}</ItemCode>" +
            "<ItemAmount>1234.34</ItemAmount>" +
            " <LeadBankCode>WEMA</LeadBankCode>" +
            " <LeadBankCbnCode>035</LeadBankCbnCode>" +
            " <LeadBankName>WEMA Bank Plc</LeadBankName>" +
            " <CategoryCode>100345678</CategoryCode>" +
            " <CategoryName></CategoryName>" +
            "</PaymentItem>" +
            "</PaymentItems>" +
            "<ProductGroupCode>09/21/2018 07:00:36</ProductGroupCode>" +
            "<PaymentStatus>0</PaymentStatus>" +
            "<IsReversal>False</IsReversal>" +
            "<SettlementDate>09/22/2018 07:00:36</SettlementDate>" +
            "<FeeName>" +
            "</FeeName>" +
            "<ThirdPartyCode>STB</ThirdPartyCode>" +
            "<OriginalPaymentLogId></OriginalPaymentLogId>" +
            "<OriginalPaymentReference></OriginalPaymentReference>" +
            "<Teller>Oluwaranti Adebowale</Teller>" +
            "</Payment>" +
            "</Payments>" +
            "</PaymentNotificationRequest>";

    @Test
    public void parseCustomerInformationRequest() throws Exception {
        CustomerInformationRequest customerInformationRequest = CustomerInformationExecutor.unmarshallCustomerInformationRequest(CUSTOMER_INFO_REQUEST);
        assertEquals("6033", customerInformationRequest.getMerchantReference());
        assertEquals("01", customerInformationRequest.getPaymentItemCode());
        assertEquals("STB", customerInformationRequest.getThirdPartyCode());
    }

    @Test
    public void parsePaymentNotificationRequest() throws Exception {
        PaymentNotificationRequest paymentNotificationRequest =  PaymentNotificationExecutor.unmarshallNotificationRequest(PAYMENT_NOTIFICATION_REQUEST);
        Payment payment = paymentNotificationRequest.getPaymentList().get(0);
        assertEquals(new BigDecimal("101.00").longValue(), payment.getAmount().longValue());
        assertEquals("BOND|TEST|BRH|2020-11-10|1230909019", payment.getPaymentReference());
        assertEquals(Boolean.FALSE, payment.getIsReversal());
        PaymentNotificationItem paymentItem = payment.getPaymentItemList().get(0);
        assertEquals("TEST MERCHANT", paymentItem.getItemName());
    }
}

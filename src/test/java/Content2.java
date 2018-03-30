public class Content2{
    private static String template = ""
        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://com.ztesoft.zsmart/xsd\">\n"
        + "   <soapenv:Header>\n"
        + "      <xsd:AuthHeader>\n"
        + "         <Username>${username}</Username>\n"
        + "         <Password>${password}</Password>\n"
        + "      </xsd:AuthHeader>\n"
        + "   </soapenv:Header>\n"
        + "   <soapenv:Body>\n"
        + "      <xsd:QueryProfileAndBalRequest>\n"
        + "         <MSISDN>${msisdn}</MSISDN>\n"
        + "         <TransactionSN>${transactionSN}</TransactionSN>\n"
        + "         <UserPwd>${userPwd}</UserPwd>\n"
        + "      </xsd:QueryProfileAndBalRequest>\n"
        + "   </soapenv:Body>\n"
        + "</soapenv:Envelope>\n";
}

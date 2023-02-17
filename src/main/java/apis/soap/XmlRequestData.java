package apis.soap;

public class XmlRequestData {
    public String buildXmlToGetMailMimeContent(String mail, String mailId){

        return String.format(
                XmlTemplate.xmlToGetMailMimeContentTemp,
                mail,
                mailId
        );
    }
}


class XmlTemplate {
    public static String xmlToGetMailMimeContentTemp = """
            <?xml version="1.0" encoding="utf-8"?>
              <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:m="http://schemas.microsoft.com/exchange/services/2006/messages" xmlns:t="http://schemas.microsoft.com/exchange/services/2006/types" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Header>
                  <t:RequestServerVersion Version="Exchange2007_SP1" />
                  <t:TimeZoneContext>
                    <t:TimeZoneDefinition Id="China Standard Time" />
                  </t:TimeZoneContext>
                  <t:ExchangeImpersonation>
                    <t:ConnectingSID>
                      <t:PrimarySmtpAddress>%s</t:PrimarySmtpAddress>
                    </t:ConnectingSID>
                  </t:ExchangeImpersonation>
                </soap:Header>
                <soap:Body>
                  <m:GetItem>
                    <m:ItemShape>
                      <t:BaseShape>AllProperties</t:BaseShape>
                      <t:AdditionalProperties>
                        <t:FieldURI FieldURI="item:MimeContent" />
                      </t:AdditionalProperties>
                    </m:ItemShape>
                    <m:ItemIds>
                      <t:ItemId Id="%s" />
                    </m:ItemIds>
                  </m:GetItem>
                </soap:Body>
              </soap:Envelope>
            """;
}

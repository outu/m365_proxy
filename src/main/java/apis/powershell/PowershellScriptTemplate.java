package apis.powershell;

public class PowershellScriptTemplate {
    public static String authExchange = """
                $password = ConvertTo-SecureString "%s" -AsPlainText -Force
                $UserCredential = New-Object System.Management.Automation.PSCredential ("%s", $password)
                $Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri %s://%s/PowerShell/ -Authentication Kerberos -Credential $UserCredential
                """;

    public static String getExchangeMailBox = """
            $output = Import-PSSession $Session -DisableNameChecking
            Get-Mailbox -ResultSize Unlimited |select displayname,PrimarySmtpAddress
            """;
}

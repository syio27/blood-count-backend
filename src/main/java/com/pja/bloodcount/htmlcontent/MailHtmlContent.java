package com.pja.bloodcount.htmlcontent;

/**
 * Constant Mail Message with HTML content
 */
public class MailHtmlContent {

    /**
     * Base HTML letter template
     * @param text -> use MailMessageConstant inside the html content
     * @param url -> path to page, pass null if showButton false
     * @param buttonLabel -> label of button, pass null if showButton false
     * @param showButton -> true if want to show the footer button, false otherwise
     * @return HTML content
     */
    public static String getHtmlMessage(String text, String url, String buttonLabel, boolean showButton) {
        String button = "";

        if (showButton) {
            button = """
                    <tr>
                        <td align="center" style="padding:20px 20px 50px 20px;">
                            <a href="%s" style="display: inline-block; background-color: #31CB4B; border-radius: 30px; font-size: 18px; color: #FFFFFF; text-decoration: none; padding: 10px 40px;">%s</a>
                        </td>
                    </tr>
                    """.formatted(url, buttonLabel);
        }

        return """
                <body data-new-gr-c-s-loaded="14.1129.0" style="width:100%%; font-family:arial, 'helvetica neue', helvetica, sans-serif; background-color:#F6F6F6;">
                    <div class="es-wrapper-color" style="width:100%%; height:100%%; background-color:#F6F6F6;">
                        <table class="es-wrapper" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse; border-spacing:0px;">
                            <tr>
                                <td valign="top">
                                    <table class="es-header" cellspacing="0" cellpadding="0" align="center" style="table-layout:fixed; width:100%%;">
                                        <tr>
                                            <td align="center">
                                                <table class="es-header-body" width="500" cellspacing="0" cellpadding="0" align="center" style="background-color:#FFFFFF;">
                                                    <tr>
                                                        <td align="center" style="padding:40px 20px 20px 20px;">
                                                           <img src="cid:image" alt="Logo" style="width: 60px;">
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="left" style="padding:20px 20px 20px 20px;">
                                                         <p style="margin:0; color:#333333; font-size:14px; line-height:150%%;">
                                                         %s                   	
                                                        </p>
                                                        </td>
                                                    </tr>
                                                    %s
                                                </table>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </div>
                </body>
                """.formatted(text, button);
    }
}

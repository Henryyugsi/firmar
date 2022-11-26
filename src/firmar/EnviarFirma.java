
package firmar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class EnviarFirma {
    public void validarcomprobanteprueba(String xmlPathout, String URLpruebas) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        File file = new File(xmlPathout);
         byte[] archxml = Files.readAllBytes(file.toPath()); 
        String encodedString = Base64.getMimeEncoder().encodeToString(archxml);   
        String params1 = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "      <Body>" +
            "          <validarComprobante xmlns=\"http://ec.gob.sri.ws.recepcion\">" +
            "              <xml xmlns=\"\">"+encodedString+"</xml>" +
            "          </validarComprobante>" +
            "      </Body>" +
            "    </Envelope>";
        try {
            URL url=new URL(URLpruebas);
            HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setDoOutput(true);
            conn.setUseCaches(true);
	    conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/xml");
	    conn.setRequestProperty("Content-Type", "application/xml");
            try ( // Enviar XML
                    OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = params1.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                StringBuilder response;
                try ( // Leer XML
                        InputStream inputStream = conn.getInputStream()) {
                    byte[] res = new byte[2048];
                    int i = 0;
                    response = new StringBuilder();
                    while ((i = inputStream.read(res)) != -1) {
                        response.append(new String(res, 0, i));
                    }
                }
                //Guardar Firma
                Element raiz = new Element("factura");
                 Document document = new Document(raiz);
                 raiz.setText(response.toString());
                 XMLOutputter xm=new XMLOutputter();
                 xm.setFormat(Format.getPrettyFormat());
                 xm.output(document, new FileWriter("FACTURA1.xml"));
            }else{
                System.err.println("HTTP: " + conn.getResponseMessage());
            }

        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
    public void autorizarcomprobante(String claveAccesoin, String URLpruebas) throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
        String claveAcceso = claveAccesoin;
    String params1 = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "      <soap:Body>" +
            "          <ns2:autorizacionComprobante xmlns:ns2=\"http://ec.gob.sri.ws.autorizacion\">" +
            "              <claveAccesoComprobante>"+claveAcceso+"</claveAccesoComprobante>" +
            "          </ns2:autorizacionComprobante>" +
            "      </soap:Body>" +
            "    </soap:Envelope>";
        try {
            URL url=new URL(URLpruebas);
            HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setDoOutput(true);
            conn.setUseCaches(true);
	    conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/xml");
	    conn.setRequestProperty("Content-Type", "application/xml");
            try ( // Enviar XML
                    OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = params1.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                StringBuilder response;
                try ( // Read XML
                        InputStream inputStream = conn.getInputStream()) {
                    byte[] res = new byte[2048];
                    int i = 0;
                    response = new StringBuilder();
                    while ((i = inputStream.read(res)) != -1) {
                        response.append(new String(res, 0, i));
                    }
                }
                //Guardar Firma
                 Element raiz = new Element("factura");
                 Document document = new Document(raiz);
                 raiz.setText(response.toString());
                 XMLOutputter xm=new XMLOutputter();
                 xm.setFormat(Format.getPrettyFormat());
                 xm.output(document, new FileWriter("FACTURA.xml"));
            }else{
                System.err.println("HTTP: " + conn.getResponseMessage());
            }

        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}

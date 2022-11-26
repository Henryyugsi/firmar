
package firmar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;


public class Firmar {
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, TransformerException {
        String xmlPath = "C:\\Users\\henry\\Desktop\\FACTURASELECTRONICAS\\FACTURA-001001000000005.xml";
        String pathSignature = "D:\\Firma Electronica\\Token\\995777980392856793948811129.p12";
        String passSignature = "Henryyugsin03*";
        String xmlPathout = "C:\\Users\\henry\\Desktop\\FACTURASELECTRONICAS\\FACTURAR-001001000000005.xml";
        String URLpruebas= "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
        String URLpruebasAutorizacion= "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
        String claveAcceso="2409202201185002240900110010010000000051234567815";
        Conexion c1 = new Conexion();
        c1.conectar(0);
        GenericXMLSignature firm = new GenericXMLSignature(xmlPath,pathSignature,passSignature,xmlPathout);
        firm.realizarfirma();
        //ValidarFirma al SRI
        EnviarFirma enviar=new EnviarFirma();
        enviar.validarcomprobanteprueba(xmlPathout, URLpruebas);
        //Autorizar firma al SRI
        enviar.autorizarcomprobante(claveAcceso, URLpruebasAutorizacion);
        
        
    }
    
}

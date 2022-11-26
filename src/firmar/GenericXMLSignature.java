
package firmar;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GenericXMLSignature {
    public String xmlPath;
    public String pathSignature;
    public String passSignature;
    public String xmlPathout;
    public GenericXMLSignature(String xmlPath,String pathSignature, String passSignature,String xmlPathout) {
        this.xmlPath=xmlPath;
        this.pathSignature=pathSignature;
        this.passSignature=passSignature;
        this.xmlPathout=xmlPathout;
    }
    public Document obtenerdocumento() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{
        DocumentBuilderFactory dbf =   DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        File file = new File(this.xmlPath);
        Document doc = builder.parse(new FileInputStream(file));
        return doc;
    }
    public KeyStore leerclave() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore ks = null;
        ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(this.pathSignature), this.passSignature.toCharArray());
        java.security.Provider pro = ks.getProvider();
    return ks;
    }
    public String alias() throws IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException{
    String alias = null;
    try
    {
      Enumeration nombres = leerclave().aliases();
      while (nombres.hasMoreElements())
      {
        String tmpAlias = (String)nombres.nextElement();
        if (leerclave().isKeyEntry(tmpAlias)) {
          alias = tmpAlias;
        }
      }
    }
    catch (KeyStoreException e)
    {
      System.err.println("Error: " + e.getMessage());
    }
    return alias;
    }
    public X509Certificate generarX509() throws IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException{
    String alias = alias();
    X509Certificate certificate = null;
    try
    {
      certificate = (X509Certificate)leerclave().getCertificate(alias);
      if (certificate == null)
      {
        System.err.println("No existe ningun certificado para firmar.");
      }
    }
    catch (KeyStoreException e1)
    {
        System.err.println("Error: " + e1.getMessage());
    }
    return certificate;
    }
        
    public PublicKey obtenerclavepublica() throws KeyStoreException, IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{
        PublicKey publicKey=null;
        publicKey = generarX509().getPublicKey();
        return publicKey;
    }
    
    public PrivateKey obtenerclaveprivada() throws KeyStoreException, IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{
    PrivateKey privateKey = null;
    KeyStore tmpKs = leerclave();
    privateKey = (PrivateKey)tmpKs.getKey(alias(), this.passSignature.toCharArray());
    return privateKey;
    }

    protected DataToSign createDataToSign() throws ParserConfigurationException, SAXException, IOException{
     DataToSign datosAFirmar = new DataToSign();
     datosAFirmar.setXadesFormat(EnumFormatoFirma.XAdES_BES);
     datosAFirmar.setEsquema(XAdESSchemas.XAdES_132);
     datosAFirmar.setXMLEncoding("UTF‐8");
     datosAFirmar.setEnveloped(true);
     datosAFirmar.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "compel", null, "text/xml",null));
     datosAFirmar.setParentSignNode("comprobante");
     Document docToSign = obtenerdocumento();
     datosAFirmar.setDocument(docToSign);
     return datosAFirmar;
}
    public void realizarfirma() throws ParserConfigurationException, SAXException, IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
    DataToSign dataToSign = createDataToSign();
    Provider provider1 = Security.getProvider("SunRsaSign");
    X509Certificate certificate = generarX509();
    PrivateKey pk=obtenerclaveprivada();
    FirmaXML firma = new FirmaXML();
    Document docSigned = null;
    try{
      Object[] res;
      res = firma.signFile(certificate, dataToSign,pk,SignatureMethod.RSA_SHA1,provider1);  
      docSigned = (Document)res[0];
    }catch (Exception ex){
      System.err.println("Error realizando la firma: " + ex.getMessage());
    }
    //Guardar Documento
    //Generar el path de salida
    
    try{
      DOMSource source = new DOMSource(docSigned);
      StreamResult result = new StreamResult(new File(this.xmlPathout));
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(source, result);
    }
    catch (TransformerConfigurationException e)
    {
        System.err.println("Error: " + e.getMessage());
    }
    catch (TransformerException e)
    {
        System.err.println("Error: " + e.getMessage());
    }
    }

}

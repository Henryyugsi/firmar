
package firmar;


import java.net.URL;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xml.security.Init;
public class Conexion {
    static Logger log = Logger.getLogger(Conexion.class);
    URL url = Conexion.class.getResource("Log4j.properties");
    public void conectar(int num){
        PropertyConfigurator.configure(url);
    if (!Init.isInitialized()){
    //conexion exitosa
    log.info("conexion exitosa");
    Init.init();
    }else if(num>0){
       //precaucion
       log.warn("precaucion");
    }else{
    //fatal error
    log.fatal("fallo total");
    }
    
    }
}
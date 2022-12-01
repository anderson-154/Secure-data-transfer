# Transferencia de archivos con chequeo de integridad con clave pública.
---
## Desarrolladores

- Benjamin Silva:  [ben331](https://github.com/ben331)
- Anderson Cardenas: [anderson-154](https://github.com/anderson-154)
- Samuel Viviescas: [SamujV](https://github.com/SamujV)

---
## Objetivo del proyecto: ##
Deben desarrollarse dos programas, un cliente y un servidor. El programa servidor debe escuchar por un puerto determinado, y esperar la conexión del cliente. El cliente recibe un nombre de archivo como parámetro. Una vez conectados cliente y servidor, el servidor debe generar un par de claves RSA (pública y privada), y mandar la pública al cliente. El cliente debe entonces cifrar el archivo con la clave pública recibida, y transferirlo al servidor, quien procederá a descifrarlo con la respectiva clave privada. Al final del proceso el cliente debe calcular el hash SHA-256 del archivo que acaba de transmitir, y enviarlo al servidor. El servidor debe calcular el hash sobre el archivo recibido, y compararlo con el hash recibido del cliente. Si son iguales, debe indicarse que el archivo se transfirió adecuadamente.
---

## Realización del proyecto

---

### Lenguaje de programación

Se decidio que el lenguaje de programación por utilizar sea Java puesto que hemos trabajado en el la mayor parte de la carrera y tenemos mayor conocimiento de el.

Luego, definimos las preguntas claves para hacer una investigación:

*1. ¿Cómo definir la clave pública?*

*2. ¿Cómo cifrar con la clave pública?*

*3. ¿Cómo calcular el Hash SHA-256 en el archivo recibido?*

---

### Implementación del proyecto

#### Creación de la conexión entre cliente y servidor

Se activa el servidor a través de la instancia de un *Server Socket*, programando bajo que puerto del servidor se estará escuchando.

```
 ServerSocket server = new ServerSocket(5000);
```
Despúes, se acepta la conexión creando un *Socket*, ya que por el lado del cliente se estará enviando la petición, mientras no se envíe la petición el servidor, este se queda en espera de dicha solicitud.

```
Socket socket = server.accept();

```

En el lado del cliente, se crea un nuevo *Socket*, con la dirección IP y puerto en el que el servidor estará esperando. Dicho *Socket* es el que será aceptado por el servidor al enviar la petición.

```
Socket socket = new Socket("127.0.0.1", 5000);

```

#### Lectura y escritura

Se crean dos *Stream* en el cliente y en el servidor; un *OutputStream* para escritura y un *InputStream* para lectura. Ambos están ligados al *Socket* que brinda al programa conexión con el cliente o con el servidor.

```
OutputStream os = socket.getOutputStream();
InputStream is = socket.getInputStream();
```
*Este código genera un problema, que será mostrado al final del informe.*

---


#### 1. ¿Cómo definir la clave pública?

En el servidor, se define la instancia para generar el par de claves tipo RSA, después se accede a dicho par y se almacenan en variables independientes ambas claves.

```
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
keyPairGenerator.initialize(2048);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
PublicKey publicKey = keyPair.getPublic();
PrivateKey privateKey = keyPair.getPrivate();
```

#### 2. ¿Cómo cifrar con la clave pública?

Despúes de recibir la clave pública, en el cliente se convierte el archivo a tranferir en bytes para poder ser encriptado, y se crea una instancia de Cipher para poder cifrar. Iniciamos la encripción con esa intancia y elegimos el modo de encripción junto a la llave pública. Finalmente, se encripta el archivo utilizando la instancia de cifrado.

```
byte[] fileBytes = Files.readAllBytes(Paths.get(path));
Cipher encryptCipher = Cipher.getInstance("RSA");
encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);
```

#### 3. ¿Cómo calcular el SHA en el archivo recibido?

Para el cliente y el servidor, se calcula el SHA-256. Para esto, se utilizó directamente y sin modificación un método extraído de:  [Java File Checksum-MD5 and SHA-256 Hash Example](howtodoinjava.com).
```
MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
String shaChecksum = getFileChecksum(shaDigest, file);
```

Método getFileChecksum(shaDigest, file)

```
 private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
```



---

## Problemas en la realización del proyecto
---

### Envío de clave pública al cliente

Un problema que ocupó gran cantidad de tiempo fue el envío de la clave pública al servidor, debido a que al enviar y recibirla, el OutputStream no podía enviar o recibir archivos de cualquier formato. Además de que no eran Objetos de escritura y lectura directa. Por lo tanto se instanció para cada uno un *Buffered*, un *BufferedWriter* y un *BufferedReader*. Ambos recibiendo por parámetros los *Stream* dentro de un *OutputStreamWriter* o un *InputStreamReader*.

```
BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
BufferedReader br = new BufferedReader(new InputStreamReader(is));
```


Con esta modificación, se podía realizar la escritura y lectura con los métodos bw.write() para escritura y br.read() o br.readLine() para lectura. Sin embargo, decidimos utilizar un formato Json, que permite el envío de los bytes de forma más eficiente. Se define que vamos a enviar la clave pública en un arreglo de byte y será enviado serializando el arreglo a través de Json en una cadena de texto *String*




Se crea un Gson, se envía de forma codificada, o sea en bytes la llave pública. Y se transforma a un String a través de gson.ToJson(publicKey.getEncoded()).
Se escribe con bw.write(), y se envía al cliente con bw.flush(), no se usa bw.close() porque puede ser utilizado posteriormente.
```
Gson gson = new Gson();
String json = gson.toJson(publicKey.getEncoded());
bw.write(json+"\n");
bw.flush();
```
Para la lectura de esa llave pública, se crea un String leyendo con el br.readLine(), que toma la siguiente línea no leída. Se crea una instancia de Gson y se transforma a byte[] usando gson.fromJson(json, byte[].class), especificando el String obtenido y el tipo de clase del cual se quiere recuperar.

```
String json = br.readLine();
Gson gson = new Gson();
byte[] publicKeyBytes = gson.fromJson(json, byte[].class);
```
Como Key es la llave pública en bytes, se debe recuperar para cifrar el archivo. Volvemos a obtener un objeto de PublicKey.

```
KeyFactory keyFactory = KeyFactory.getInstance("RSA");
X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
```

La clase ***EncryptedFile***, tiene como objetivo simplificar el proceso y enviar toda la información del archivo cifrado de una sola vez, y se compone principalmente de:

###### Dos constructores, uno vacío para su eficiente envío usando Json y un constructor que recibe los bytes del archivo encriptado junto con su SHA-256.

```
public EncryptedFile(byte[] info, String SHA256) {
    this.info = info;
    this.SHA256 = SHA256;
}
```

###### Dos métodos get, encargados de retornar ambos datos necesarios en la lectura del archivo y descifrado.
```
public String getSHA256() {
    return SHA256;
}
    
public byte[] getInfo() {
    return info;
}
```
### Cifrado de archivos de mayor tamaño

Encontramos el siguiente problema cuando tratamos de cifrar archivos de más de 256:
```
javax.crypto.IllegalBlockSizeException: Data must not be longer than 256 bytes
```
Para resolver esto, encontramos 2 soluciones gracias a la investigación. 

La primera solución es cifrar el archivo con una llave simétrica, luego encriptar dicha llave con la llave RSA. Luego, enviar la llave pública, la llave simétrica cifrada y el archivo. Finalmente, descifrar con RSA la llave simétrica y con esta llave descifrar el archivo.

La seguna solución era aumentar el tamaño de la llave RSA. No obstante, por más que lo aumentaramos, solo ibamos a hacer el programa menos eficiente y el aumento en bytes que podíamos cifrar no era suficiente.

Finalmente, se decidió no optar por ninguna de las soluciones, pues no aportaban de manera significativa al proyecto.

---

## Conclusiones del proyecto y de seguridad
---

* El uso de Json para envío y recepción de cualquier tipo de archivos, simplifica el proceso de programación.
* Para un manejo más adecuado en tipos de datos y una mayor facilidad en la programación, se considera que una implementación en el lenguaje ***Python*** puede ser mejor para proyectos de seguridad. Evitando posibles errores en los tipos de datos y eliminando la creación de clases en el modelo.
* Para el problema del tamaño del archivo por cifrar, es posible pensar en una alternativa donde cifremos por partes y mandemos al servidor. Esta posibilidad se contempla para trabajos futuros.

Desde lo analizado e implementado en el proyecto, desde un aspecto de seguridad, se concluye que:

*   Existen diversas herramientas para proteger a los usuarios de actividades mal intencionadas en las aplicaciones que desarrollemos, estas herramientas son de fácil acceso y no es muy difícil cuidar a nuestros usuarios.
*   Es muy fácil recibir ataques en un mundo con gran cantidad de información privilegiada, por lo mismo siempre debemos buscar la forma de asegurar nuestros sistemas.
*   El chequeo a través de clave pública y privada genera una alta seguridad, ya que no hay un modo sencillo de que nuestra clave privada se vea expuesta. Adicionalmente, en el peor de los casos, si un agente externo malintencionado adquiere la clave pública, no significará ningún peligro.

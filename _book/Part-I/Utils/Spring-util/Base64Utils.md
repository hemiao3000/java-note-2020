# Base64Utils


Base64Utils 类来自 org.springframework.util 包。

Base64 是网络上最常见的用于传输 8 Bit 字节码的编码方式之一，Base64 就是一种基于 64 个可打印字符来表示二进制数据的方法。

Base64 编码是从二进制到字符的过程，可用于在 HTTP 环境下传递较长的标识信息。

采用 Base64 编码具有不可读性，需要解码后才能阅读。

- <big>**编码方法**</big>

  ```java
  static byte[]	encode(byte[] src)
  static String	encodeToString(byte[] src)

  static byte[]	encodeUrlSafe(byte[] src)
  static String	encodeToUrlSafeString(byte[] src)
  ```

- <big>**解码方法**</big>

  ```java
  static byte[]	decode(byte[] src)
  static byte[]	decodeFromString(String src)

  static byte[]	decodeUrlSafe(byte[] src)
  static byte[]	decodeFromUrlSafeString(String src)
  ```


**BASE64-字母表**

|字符|十进制|十六进制||字符|十进制|十六进制||字符|十进制|十六进制|
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|A| 0|00|	 	|W|22|16|	 	|s|44|2C|
|B| 1|01|	 	|X|23|17|	 	|t|45|2D|
|C| 2|02|	 	|Y|24|18|	 	|u|46|2E|
|D| 3|03|	 	|Z|25|19|	 	|v|47|2F|
|E| 4|04|	 	|a|26|1A|	 	|w|48|30|
|F| 5|05|	 	|b|27|1B|	 	|x|49|31|
|G| 6|06|	 	|c|28|1C|	 	|y|50|32|
|H| 7|07| 	    |d|29|1D|	 	|z|51|33|
|I| 8|08|	 	|e|30|1E|	 	|0|52|34|
|J| 9|09|	 	|f|31|1F|	 	|1|53|35|
|K|10|0A|	 	|g|32|20|	 	|2|54|36|
|L|11|0B|	 	|h|33|21|	 	|3|55|37|
|M|12|0C|	 	|i|34|22|	 	|4|56|38|
|N|13|0D|	 	|j|35|23|	 	|5|57|39|
|O|14|0E|	 	|k|36|24|	 	|6|58|3A|
|P|15|0F|	 	|l|37|25|	 	|7|59|3B|
|Q|16|10|	 	|m|38|26|	 	|8|60|3C|
|R|17|11|	 	|n|39|27|	 	|9|61|3D|
|S|18|12|	 	|o|40|28|	 	|+|62|3E|
|T|19|13|	 	|p|41|29|	 	|/|63|3F|
|U|20|14|	 	|q|42|2A|	 	|||	 	 
|V|21|15|	 	|r|43|2B|	 	|=|(pad)|(pad)|

在 <font color="#0088dd">**URL and Filename Safe**</font> 字母表中，`+` 号会被替换成 `-` 号，进行编解码。同理，`/` 号会被替换成 `_` 。



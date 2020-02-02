# StringUtils

StringUtils 来自 `org.springframework.util` 包

- <big>**字符串判断**</big>

  ```java
  // 判断字符串是否为 null，或""。注意，包含空白符的字符串为非空。
  static boolean isEmpty(Object str) 

  // 判断字符串是否是以指定内容结束。忽略大小写。
  static boolean endsWithIgnoreCase(String str, String suffix) 

  // 判断字符串是否已指定内容开头。忽略大小写。
  static boolean startsWithIgnoreCase(String str, String prefix) 

  // 是否包含空白符
  static boolean containsWhitespace(String str) 

  // 判断字符串非空且长度不为 0。
  static boolean hasLength(CharSequence str) 

  // 判断字符串是否包含实际内容（即非仅包含空白符）
  static boolean hasText(CharSequence str) 

  // 判断字符串指定索引处是否包含一个子串。
  static boolean substringMatch(CharSequence str, int index, CharSequence substring) 

  // 计算一个字符串中指定子串的出现次数。
  static int countOccurrencesOf(String str, String sub) 
  ```


- <big>**字符串数组操作**</big>

  ```java
  // 向参数字符串数组的末尾添加新的字符串，并返回新数组。
  static String[] addStringToArray(String[] array, String str) 

  // 将两个字符串数组合并成一个字符串数组。其中重复的元素会出现两次。
  static String[] concatenateStringArrays(String[] array1, String[] array2) 

  // 被废弃，建议通过 LinkedHashSet 手动合并两个字符串。
  static String[] mergeStringArrays(String[] array1, String[] array2) 

  // Remove duplicate strings from the given array.
  static String[] removeDuplicateStrings(String[] array) 

  // 对给定字符串数组进行排序，并返回排序后的新数组。
  static String[] sortStringArray(String[] array) 
  ```


- <big>**数组 / 集合 ==> 字符串**</big>

  ```java
  // 以 “,” 作为分隔符。
  static String arrayToCommaDelimitedString(Object[] arr)

  // 第二个参数手动指定分隔符。
  static String arrayToDelimitedString(Object[] arr, String delim)  

  // 以“,”作为分隔符。
  static String collectionToCommaDelimitedString(Collection<?> coll) 

  // 第二个参数手动指定分隔符。
  static String collectionToDelimitedString(Collection<?> coll, String delim) 

  // 集合中每一个元素的字符串前后可以加上前缀和后缀。
  static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) 
  ```

- <big>**字符串 转 数组/集合**</big>

  ```java
  // 以指定分隔符切分成字符串，切割成两份。
  static String[] split(String toSplit, String delimiter)

  // 以指定分隔符（可以是多个）切分字符串
  static String[] tokenizeToStringArray(String str, String delimiters)

  // 以“,”作为分隔符
  static Set<String> commaDelimitedListToSet(String str)

  // 以“,”作为分隔符。
  static String[] commaDelimitedListToStringArray(String str)  

  // 以指定分隔符进行切割
  static String[] delimitedListToStringArray(String str, String delimiter)  

  // 切割字符串的同时，删除指定字符（可以使多个）
  static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) 
  ```

- <big>**其他转换 **</big>

  ```java
  static String[] toStringArray(Collection<String> collection) // 将字符串集合转变为字符串数组。
  ```


### 字符“修正”

- <big>**增加新内容**</big>

  ```java
  // 加上单引号
  static String quote(String str) 

  // 同上。如果参数是非字符串，则返回参数对象。
  static Object quoteIfString(Object obj) 
  ```

- <big>**删除内容**</big>

  ```java
  // 去除尾部的特定字符。
  static String trimTrailingCharacter(String str, char trailingCharacter) 

  // 去除头部的特定字符。
  static String trimLeadingCharacter(String str, char leadingCharacter) 

  // 去除头部的空白符。
  static String trimLeadingWhitespace(String str) 

  // 去除尾部的空白符。
  static String trimTrailingWhitespace(String str) 

  // 去除头部和尾部的空白符。
  static String trimWhitespace(String str) 

  // 删除开头、结尾和中间的空白符。
  static String trimAllWhitespace(String str) 

  // 删除指定子串
  static String delete(String inString, String pattern) 

  // 删除指定字符（可以是多个）
  static String deleteAny(String inString, String charsToDelete) 

  // 对数组的每一项执行 trim() 方法。
  static String[] trimArrayElements(String[] array) 
  ```


- <big>**修改原内容**</big>

  ```java
  // 查找指定子串，替换成指定新内容。
  static String replace(String inString, String oldPattern, String newPattern) 

  // 每个单词的首字母大写。
  static String capitalize(String str) 

  // 每个单词的首字母小写。
  static String uncapitalize(String str) 

  // 将 URL 字符串进行解码
  static String uriDecode(String source, Charset charset) 
  ```


- <big>**截取**</big>

  ```java
  // 以“.”作为分隔符，获取其最后一部分。
  static String unqualify(String qualifiedName) 

  // 以指定字符作为分隔符，获取其最后一部分。
  static String unqualify(String qualifiedName, char separator) 
  ```


- <big>**文件路径字符串操作**</big>

  ```java
  // 解析路径字符串中的“..”，返回更简洁的字符串路径。
  static String cleanPath(String path)    

  // 从一个路径字符串中解析出文件名部分。
  static String getFilename(String path) 

  // 从一个路径字符串中解析出文件后缀名部分。
  static String getFilenameExtension(String path) 

  // 比较两个路径字符串是否是同一个路径名。会自动处理路径中的“..”。
  static boolean pathEquals(String path1, String path2)

  // 剥离文件路径名中后缀部分。
  static String stripFilenameExtension(String path)

  // 在一个路径（通常是绝对路径，需要以“/”结束）之后，添加相对于它为起点的相对路径文件名。
  static String applyRelativePath(String path, String relativePath)
  ```


- 其他

  ```java
  // 将字符串数组中的每一项，按照指定分隔符进行切分，并生成 Properties 对象。
  // 字符串数组的内容类似于：new String[]{"key1,value1", "key2,value2", "key3,value3"}
  static Properties splitArrayElementsIntoProperties(String[] array, String delimiter)

  // 通过解析时区字符串生成时区对象。
  // 常见 TimeZone 字符串见最后。
  static TimeZone parseTimeZoneString(String timeZoneString)
  ```

|TimeZone|地点|
|-:|:-|
|"Asia/Shanghai" | 中国标准时间 (北京)|
|"Asia/Hong_Kong" | 香港时间 (香港)|
|"Asia/Taipei" | 台北时间 (台北)|
|"Asia/Seoul" | 首尔|
|"Asia/Tokyo" | 日本时间 (东京)|
|"America/New_York" | 美国东部时间 (纽约)|
|"America/Denver" | 美国山区时间 (丹佛)|
|"America/Costa_Rica" | 美国中部时间 (哥斯达黎加)|
|"America/Chicago" | 美国中部时间 (芝加哥)|
|"America/Mexico_City" | 美国中部时间 (墨西哥城)|
|"America/Regina" | 美国中部时间 (里贾纳)|
|"America/Los_Angeles" | 美国太平洋时间 (洛杉矶)|
|"Pacific/Majuro" | 马朱罗|
|"Pacific/Midway" | 中途岛|
|"Pacific/Honolulu" | 檀香山|
|"America/Anchorage" | 安克雷奇|
|"America/Tijuana" | 美国太平洋时间 (提华纳)|
|"America/Phoenix" | 美国山区时间 (凤凰城)|
|"America/Chihuahua" | 奇瓦瓦|
|"America/Bogota" | 哥伦比亚时间 (波哥大)|
|"America/Caracas" | 委内瑞拉时间 (加拉加斯)|
|"America/Barbados" | 大西洋时间 (巴巴多斯)|
|"America/Manaus" | 亚马逊标准时间 (马瑙斯)|
|"America/St_Johns" | 纽芬兰时间 (圣约翰)|
|"America/Santiago" | 圣地亚哥|
|"America/Argentina/Buenos_Aires" | 布宜诺斯艾利斯|
|"America/Godthab" | 戈特霍布|
|"America/Montevideo" | 乌拉圭时间 (蒙得维的亚)|
|"America/Sao_Paulo" | 圣保罗|
|"Atlantic/South_Georgia" | 南乔治亚|
|"Atlantic/Azores" | 亚述尔群岛|
|"Atlantic/Cape_Verde" | 佛得角|
|"Africa/Casablanca" | 卡萨布兰卡|
|"Europe/London" | 格林尼治标准时间 (伦敦)|
|"Europe/Amsterdam" | 中欧标准时间 (阿姆斯特丹)|
|"Europe/Belgrade" | 中欧标准时间 (贝尔格莱德)|
|"Europe/Brussels" | 中欧标准时间 (布鲁塞尔)|
|"Europe/Sarajevo" | 中欧标准时间 (萨拉热窝)|
|"Africa/Brazzaville" | 西部非洲标准时间 (布拉扎维)|
|"Africa/Windhoek" | 温得和克|
|"Asia/Amman" | 东欧标准时间 (安曼)|
|"Europe/Athens" | 东欧标准时间 (雅典)|
|"Asia/Beirut" | 东欧标准时间 (贝鲁特)|
|"Africa/Cairo" | 东欧标准时间 (开罗)|
|"Europe/Helsinki" | 东欧标准时间 (赫尔辛基)|
|"Asia/Jerusalem" | 以色列时间 (耶路撒冷)|
|"Africa/Harare" | 中部非洲标准时间 (哈拉雷)|
|"Europe/Minsk" | 明斯克|
|"Asia/Baghdad" | 巴格达|
|"Europe/Moscow" | 莫斯科|
|"Asia/Kuwait" | 科威特|
|"Africa/Nairobi" | 东部非洲标准时间 (内罗毕)|
|"Asia/Tehran" | 伊朗标准时间 (德黑兰)|
|"Asia/Baku" | 巴库|
|"Asia/Tbilisi" | 第比利斯|
|"Asia/Yerevan" | 埃里温|
|"Asia/Dubai" | 迪拜|
|"Asia/Kabul" | 阿富汗时间 (喀布尔)|
|"Asia/Karachi" | 卡拉奇|
|"Asia/Oral" | 乌拉尔|
|"Asia/Yekaterinburg" | 叶卡捷林堡|
|"Asia/Calcutta" | 加尔各答|
|"Asia/Colombo" | 科伦坡|
|"Asia/Katmandu" | 尼泊尔时间 (加德满都)|
|"Asia/Almaty" | 阿拉木图|
|"Asia/Rangoon" | 缅甸时间 (仰光)|
|"Asia/Krasnoyarsk" | 克拉斯诺亚尔斯克|
|"Asia/Bangkok" | 曼谷|
|"Asia/Irkutsk" | 伊尔库茨克时间 (伊尔库茨克)|
|"Asia/Kuala_Lumpur" | 吉隆坡|
|"Australia/Perth" | 佩思|
|"Asia/Yakutsk" | 雅库茨克时间 (雅库茨克)|
|"Australia/Darwin" | 达尔文|
|"Australia/Brisbane" | 布里斯班|
|"Asia/Vladivostok" | 海参崴时间 (符拉迪沃斯托克)|
|"Pacific/Guam" | 关岛|
|"Australia/Adelaide" | 阿德莱德|
|"Australia/Hobart" | 霍巴特|
|"Australia/Sydney" | 悉尼|
|"Asia/Magadan" | 马加丹时间 (马加丹)|
|"Pacific/Auckland" | 奥克兰|
|"Pacific/Fiji" | 斐济|
|"Pacific/Tongatapu" | 东加塔布|

<span class="title">Commons IO</span>

# IOUtils 类

IOUtils 主要操作 IO 流进行文件的读写操作。

IOUtils常用方法如下：

```java
// “安静地”关闭流对象。2.6 被废弃，建议使用 try-with-resource
static void	closeQuietly(Closeable closeable) 

// 从输入流中读取数据
static String toString(InputStream input, Charset encoding) 

// 同上
static String toString(InputStream input, String encoding)
```

```java
/* 从输入流 拷贝至 输出流 */
static int copy(InputStream input, OutputStream output)
static void copy(InputStream input, Writer output, Charset inputEncoding)
static void copy(Reader input, OutputStream output, String outputEncoding)
static void copy(Reader input, OutputStream output, Charset outputEncoding)
```

```java
/* 从 输入流 中读取字节 */
static byte[]	toByteArray(InputStream input)
static byte[]	toByteArray(InputStream input, int size)
static byte[]	toByteArray(InputStream input, long size)
// 2.5 中被废弃，使用下个方法替代
static byte[]	toByteArray(Reader input) 
static byte[]	toByteArray(Reader input, Charset encoding)
static byte[]	toByteArray(Reader input, String encoding)
```

```java
/* write() */
static void	write(byte[] data, OutputStream output)
static void	write(char[] data, OutputStream output, Charset encoding)
static void	write(char[] data, OutputStream output, String encoding)

static void	write(char[] data, Writer output)
static void	write(byte[] data, Writer output, Charset encoding)
static void	write(byte[] data, Writer output, String encoding)

static void	write(CharSequence data, Writer output)
static void	write(CharSequence data, OutputStream output, Charset encoding)
static void	write(CharSequence data, OutputStream output, String encoding)

static void	write(String data, Writer output)
static void	write(String data, OutputStream output, Charset encoding)
static void	write(String data, OutputStream output, String encoding)
```


```java
/* toInputStream() */
// 生成 InputStream 对象，并写入参数内容
static InputStream toInputStream(CharSequence input, Charset encoding)  
static InputStream toInputStream(CharSequence input, String encoding)

static InputStream toInputStream(String input, Charset encoding)
static InputStream toInputStream(String input, String encoding)
```

```java
static List<String>	readLines(Reader input)   // 按行读入内容。
static List<String>	readLines(InputStream input, Charset encoding)
static List<String>	readLines(InputStream input, String encoding)
```

```java
static long copyLarge(InputStream input, OutputStream output) // 用于超过 2 GB 的数据拷贝
static long	copyLarge(InputStream input, OutputStream output, byte[] buffer)
static long	copyLarge(InputStream input, OutputStream output, long inputOffset, long length)
static long	copyLarge(InputStream input, OutputStream output, long inputOffset, long length, byte[] buffer)

static long	copyLarge(Reader input, Writer output)
static long	copyLarge(Reader input, Writer output, char[] buffer)
static long	copyLarge(Reader input, Writer output, long inputOffset, long length)
static long	copyLarge(Reader input, Writer output, long inputOffset, long length, char[] buffer)
```

```java
static LineIterator	lineIterator(Reader reader) // 生成 Iterator 迭代器。可逐行读取文件内容。
static LineIterator	lineIterator(InputStream input, String encoding)
static LineIterator	lineIterator(InputStream input, Charset encoding)
```

```java
/* readFully() */
static void	readFully(InputStream input, byte[] buffer) // 读取指定数量的内容，或失败。
static void	readFully(InputStream input, byte[] buffer, int offset, int length)

static void	readFully(Reader input, char[] buffer)
static void	readFully(Reader input, char[] buffer, int offset, int length)

static byte[]	readFully(InputStream input, int length)
```

# FileUtils 类

```java
// 递归地删除文件/文件夹
static void	deleteDirectory(File directory) 
```

```java
// 将文件中的内容读入字符串
static String readFileToString(File file, Charset encoding) 
static String readFileToString(File file, String encoding)
```

```java
// 删除一个文件。无论如何不会抛出异常。
static boolean deleteQuietly(File file) 
```

```java
static void	copyFile(File srcFile, File destFile) // 拷贝文件，保留源文件的日期
static void	copyFile(File srcFile, File destFile, boolean preserveFileDate) // 是否保留文件日期取决于第三个参数
static long	copyFile(File input, OutputStream output)
```

```java
static void	writeStringToFile(File file, String data, Charset encoding) // 向文件中写入字符串。若文件不存在，则创建文件。
static void	writeStringToFile(File file, String data, Charset encoding, boolean append)
static void	writeStringToFile(File file, String data, String encoding)
static void	writeStringToFile(File file, String data, String encoding, boolean append)
```

```java
static void	forceMkdir(File directory) // 创建文件夹
static void	forceMkdirParent(File file) // 创建文件夹，及其父文件夹。
```

```java
static void	write(File file, CharSequence data, Charset encoding)
static void	write(File file, CharSequence data, String encoding)
static void	write(File file, CharSequence data, Charset encoding, boolean append)
static void	write(File file, CharSequence data, String encoding, boolean append)
```

```java
static Collection<File>	listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
// Finds files within a given directory (and optionally its subdirectories).
static Collection<File>	listFiles(File directory, String[] extensions, boolean recursive)
// Finds files within a given directory (and optionally its subdirectories) which match an array of extensions.
static Collection<File>	listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
// Finds files within a given directory (and optionally its subdirectories).
```

```java
static void	copyDirectory(File srcDir, File destDir) // Copies a whole directory to a new location preserving the file dates.
static void	copyDirectory(File srcDir, File destDir, boolean preserveFileDate) // Copies a whole directory to a new location.
static void	copyDirectory(File srcDir, File destDir, FileFilter filter) // Copies a filtered directory to a new location preserving the file dates.
static void	copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) // Copies a filtered directory to a new location.
```

```java
static void	forceDelete(File file) // Deletes a file.
```

# FilenameUtils 类

```java
static String	getExtension(String filename) // Gets the extension of a filename.
static String	getBaseName(String filename) // Gets the base name, minus the full path and extension, from a full filename.
static String	getName(String filename) // Gets the name minus the path from a full filename.
static String	concat(String basePath, String fullFilenameToAdd) // Concatenates a filename to a base path using normal command line style rules.
static String	removeExtension(String filename) // Removes the extension from a filename.
static String	normalize(String filename) // Normalizes a path, removing double and single dot path steps.
static boolean	wildcardMatch(String filename, String wildcardMatcher) // Checks a filename to see if it matches the specified wildcard matcher, always testing case-sensitive.
static String	separatorsToUnix(String path) // Converts all separators to the Unix separator of forward slash.
static String	getFullPath(String filename) // Gets the full path from a full filename, which is the prefix + path.
static boolean	isExtension(String filename, String extension) //Checks whether the extension of the filename is that specified.
```

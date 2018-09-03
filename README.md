# PDF to Excel Converter
Convert pdf to excel. Only the text will be extracted.

## 1. Using java application
You can use the java application (in org/eadge/extractpdfexcel/0.1 directory) to convert one pdf file into excel format.
```
java -jar extractpdfexcel-0.1.jar source.pdf result.xcl
```

To specify some options, you can consult the help. You may want to specify column and row width.
```
java -jar extractpdfexcel-0.1.jar
```
## 2. Convert using java
### 2.1 Import in your java project with Maven
Add the repository:
```
<repository>
    <id>Extract-PDF-Excel</id>
    <url>https://raw.githubusercontent.com/eadgyo/Extract-PDF-Excel/master/</url>
</repository>
```
And the dependency:
```
<dependency>
    <groupId>org.eadge</groupId>
    <artifactId>extractpdfexcel</artifactId>
    <version>0.1</version>
</dependency>
```
### 2.2 One step conversion
You can convert your pdf into an Excel file in java application.
```
PdfConverter.createExcelFile("File.pdf", "File.xcl");
```
### 2.3 Four steps conversion
You can also execute each steps, in case you would like to access to data before creating the file.
#### 2.3.1 Extract Data
Text information is extracted in keeps in blocks.
```
ExtractedData extractedData = PdfConverter.extractFromFile(sourcePDFPath, textBlockIdentifier);
```
#### 2.3.2 Sort Data
Blocks are sorted, lines and columns are created.
```
SortedData sortedData = PdfConverter.sortExtractedData(extractedData, lineAxis, columnAxis, true);
```
#### 2.3.3 Create XCL Pages
2D array containing text blocks are created.
```
ArrayList<XclPage> excelPages = PdfConverter.createExcelPages(sortedData);
```
#### 2.3.4 Create sheets
Using POI Library, you can create xcl sheets.
```
HSSFSheet excelSheet = PdfConverter.createExcelSheet("sheetName", workbook, excelPage);
```
### 2.4 Visualize XCLPage
You can also visualize all the created excel sheets.
```
PdfConverter.displayXCLPages(excelPages);
```

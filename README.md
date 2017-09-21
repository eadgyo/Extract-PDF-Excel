# PDF to Excel Converter
Convert pdf text information to excel.

## One step conversion
You can convert your pdf into an Excel file.
```
PdfConverter.createExcelFile("File.pdf", "File.xcl");
```

## Four steps conversion
You can also do the 4 steps to create your xcl sheets.
### Extract Data
Text information is extracted in keeps in blocks.
```
ExtractedData extractedData = PdfConverter.extractFromFile(sourcePDFPath, textBlockIdentifier);
```

You can remove duplicate data.
```
extractedData.cleanDuplicatedData();
```

You can also try to merge near blocks.
```
extractedData.mergeBlocks();
```


### Sort Data
Blocks are sorted, lines and columns are created.
```
SortedData sortedData = PdfConverter.sortExtractedData(extractedData, lineAxis, columnAxis, true);
```

### Create XCL Pages
2D array containing text blocks are created.
```
ArrayList<XclPage> excelPages = PdfConverter.createExcelPages(sortedData);
```

### Create sheets
Using POI Library, you can create xcl sheets.
```
HSSFSheet excelSheet = PdfConverter.createExcelSheet("sheetName", workbook, excelPage);
```

## Visualize XCLPage
You can also visualize all the created excel sheets.
```
PdfConverter.displayXCLPages(excelPages);
```
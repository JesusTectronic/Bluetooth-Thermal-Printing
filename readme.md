```java
BluethoothPrinter printer = 
    new BluethoothPrinter("02:00-00-00-00-00");

try {
  printer.open(1000);
  printer.writeString("CLS\n" +
    "SIZE 51.0 mm, 25.0 mm\n" +
    "GAP 3.0 mm, 0.0 mm\n" +
    "DIRECTION 0,0\n" +
    "REFERENCE 0,0\n" +
    "OFFSET 0.0 mm\n" +
    "CLS\n" +
    "CODEPAGE UTF-8\n" +
    "TEXT 23, 18, \"D.FNT\", 0, 1, 1, \"DESCRIPTION\"\n" +
    "TEXT 290, 85, \"B.FNT\", 0, 1, 1, \"01/01/2023\"\n" +
    "TEXT 40, 82, \"4\", 0, 1, 0, \"$ 20.99\"\n" +
    "BARCODE 22,135 ,\"128M\" ,36 ,2 ,0 ,2 ,4 ,\"1234567890\"\n" +
    "PRINT 1, 1\n")

} finally {
  printer.close(2000);
}
```
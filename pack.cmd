mkdir .\out
jar cvfm ./out/app.jar Manifest.txt ./sample/*.class
mkdir .\out\lib
xcopy .\src\lib\* .\out\lib /Y
pause
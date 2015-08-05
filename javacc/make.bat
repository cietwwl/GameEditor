mkdir com\pip\sanguo\data\quest\pqe
del /Q com\pip\sanguo\data\quest\pqe
del pqe.jj
call jjtree pqe.jjt
copy *.java com\pip\sanguo\data\quest\pqe
call javacc -OUTPUT_DIRECTORY:com/pip/sanguo/data/quest/pqe pqe.jj
javac com\pip\sanguo\data\quest\pqe\*.java
pause
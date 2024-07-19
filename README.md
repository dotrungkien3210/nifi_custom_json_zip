# Nén trường Json

<aside>
💡 Với những trường có datatype dạng clob (large paragraph), đang được report là không được hỗ trợ khi transform. Ta mong muốn viết một custom processor để xử lý

</aside>

Mục đích:

Processor này được viết để xử lý một trường cụ thể chứa thông tin dạng string thông qua hoạt động kéo thả của nifi.

## Parameter

`FLOW_FORMAT`: Cái này chỉ bản ghi đầu vào sẽ có format như thế nào, hiện tại đang chấp nhận 2 format là AVRO và JSON
`AVRO_SCHEMA` schema json để kiểm tra xem bản ghi đầu vào liệu đã đủ số trường, nếu không đủ thì sẽ không xử lý
`FIELD_NAMES` : Trường trong json mà cần phải thực hiện action trên nó,hiện tại đang chỉ support cho các action dạng String
`ACTION` : hoạt động thực hiện trên paragraph hiện tại, hiện đang chấp nhận 2 input là `Replace`  và `Substring`
`FIRST_INPUT` : input đầu tiên cần điền vào

`SECOND_INPUT` : input thứ 2 cần điền vào

Ví dụ như với Action là Replace thì có thể điền First_input là “Đoạn String cần replace” và Second_input là “đoạn String sẽ được thay thế”

## **Deploy**

Clone this repository

```
git clone git@github.com:dotrungkien3210/nifi_custom_json_zip.git
```


Build

```
mvn validate
mvn clean package
```

Copy Nar file tới thư mục l $NIFI_HOME/lib

```
cp nifi-modify-value-bundle/target/nifi-modify-value-nar-$version.nar $NIFI_HOME/lib/

```

Start/Restart Nifi

```
$NIFI_HOME/bin/nifi.sh start
```

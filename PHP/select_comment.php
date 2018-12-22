<?php
$con= mysqli_connect("localhost", "root", "qkrwns12","alido");

if(mysqli_connect_errno($con))
{
    echo "Failed to connect to Mysql: ".mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$res = mysqli_query($con, "select * from comment");
$result = array();

while($row = mysqli_fetch_array($res)){
    
    array_push($result, 
            array('id'=>$row[0],'post_id'=>$row[1],'content'=>$row[2],'password'=>$row[3]));
}
echo json_encode(array("result"=>$result));
mysqli_close($con);
?>
<?php
	$user=$_POST["user"];
    $image=$_POST["image"];
    $targetImage=$_POST["targetImage"];
    
    if (!file_exists("/images/".$user)) {
    	mkdir("images/".$user, 0700);
    } 
    
    //count images in a specified directory
    $fi = new FilesystemIterator("images/".$user."/", FilesystemIterator::SKIP_DOTS);
    $count=iterator_count($fi);
    
    $decodedImage= base64_decode("$image");
    file_put_contents("images/".$user."/".$count."_".$targetImage.".png",$decodedImage);
    
    echo "okFatto";
?>
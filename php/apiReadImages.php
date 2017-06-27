<?php
	$action=$_GET["action"];
    
    switch($action){
    	case "getCountFiles":
        	$user=$_GET["user"];
        	echo getCountFiles($user);
        	break;
        case "usersName":
        	echo getUsersNames();
            break;
        case "filesName":
        	$user=$_GET["user"];
        	echo getFilesName($user);
        	break;
        case "uploadNet":
        	$net=$_POST["net"];
            echo uploadNet($net);
        	break;
        case "downloadNet":
            echo downloadNet();
        	break;
        case "test":
        	//deleting all draw (not digit) of giuliana
            foreach (new DirectoryIterator("images/roberta") as $file) {
              if ($file->isFile()) {
                  $name="images/roberta/".$file->getFilename();
                  $exploded = multiexplode(array("_","."),$name);
                  if(intval($exploded[1])>=10)
                  	unlink($name);
                  //echo $exploded[1]."<br><br><br>";
              }
            }
            echo "finito";
        	break;
        default:
        	echo "request not handled";
            break;
    }
    
    function multiexplode ($delimiters,$string) {
        $ready = str_replace($delimiters, $delimiters[0], $string);
        $launch = explode($delimiters[0], $ready);
        return  $launch;
    }

    
    function getUsersNames(){
        $usernames = scandir("images");
        $parse="";
        for ($i=2;$i<count($usernames);$i++) {
            $parse= $parse . $usernames[$i] .";";
        }
    	print_r($parse);
    }
    
    function getCountFiles($user){
    	$fi = new FilesystemIterator("images/".$user."/", FilesystemIterator::SKIP_DOTS);
		return iterator_count($fi);
    }
    
    function getFilesName($user){
    	$names="";
        foreach (new DirectoryIterator("images/".$user) as $file) {
          if ($file->isFile()) {
              $names=$names.$user."/". $file->getFilename() . ";";
          }
        }
    	return $names;
    }
    
    function uploadNet($net){
    	$decodedNet= base64_decode("$net");
    	file_put_contents("net.csv",$decodedNet);
        return "upload ok";
    }
    
    function downloadNet(){
    	$myfile = fopen("net.csv", "r");
        $text= fread($myfile,filesize("net.csv"));
        fclose($myfile);
        return $text;
    }
?>
<?php
	header("Content-type: text/html");
	echo "";
	echo "<html><body>";
	echo shell_exec("./status.sh");
	echo "</body></html>";
?>

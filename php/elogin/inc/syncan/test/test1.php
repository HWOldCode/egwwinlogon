<?php

	// Inc
	require_once('../hw.syncan.inc.php');

	// test list
	$fl = new CSyncanFileList();
	$fl->setScannerSource(new CSyncanScannerLocal('../../../../', CSyncanScanner::TYPE_SOURCE));
	$fl->setScannerDestination(new CSyncanScannerLocal('../../', CSyncanScanner::TYPE_DESTINATION));

	$lists = $fl->getLists();

	echo var_export($lists, true);
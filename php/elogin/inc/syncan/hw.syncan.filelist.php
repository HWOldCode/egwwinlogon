<?php

	/**
	 * CSyncanFileList
	 */
	class CSyncanFileList {

		// MAIN KEYs
		const MKEY_SOURCE		= 'source';
		const MKEY_DESTINATION	= 'destination';

		const KEY_SCANNER	= 'scanner';
		const KEY_STATUS		= 'status';
		const KEY_FILEENTRY	= 'fileentry';

		const STATUS_NOT_IN_DEST	= 1;
		const STATUS_NOT_IN_SOUR	= 2;
		const STATUS_FILE_CHANGE	= 3;

		/**
		 * scanner source
		 * @var CSyncanScanner
		 */
		protected $_scanner_source = null;

		/**
		 * scanner destination
		 * @var CSyncanScanner
		 */
		protected $_scanner_destination = null;

		/**
		 * __construct
		 */
		public function __construct() {

		}

		/**
		 * setScannerSource
		 * @param CSyncanScanner $source
		 */
		public function setScannerSource($source) {
			if( $source instanceof CSyncanScanner ) {
				$this->_scanner_source = $source;
			}
		}

		/**
		 * getScannerSource
		 * @return null|CSyncanScanner
		 */
		public function getScannerSource() {
			return $this->_scanner_source;
		}

		/**
		 * setScannerDestination
		 * @param CSyncanScanner $destination
		 */
		public function setScannerDestination($destination) {
			if( $destination instanceof CSyncanScanner ) {
				$this->_scanner_destination = $destination;
			}
		}

		/**
		 * getScannerDestination
		 * @return null|CSyncanScanner
		 */
		public function getScannerDestination() {
			return $this->_scanner_destination;
		}

		/**
		 * _quickSort
		 * @param array $array
		 */
		protected function _quickSort($array) {
			if( !count($array) ) {
				return $array;
			}

			$pivot	= $array[0];
			$low	= array();
			$high	= array();
			$length = count($array);

			for( $i=1; $i<$length; $i++ ) {
				if( $array[$i] <= $pivot ) {
					$low [] = $array[$i];
				}
				else {
					$high[] = $array[$i];
				}
			}

			return array_merge(
				$this->_quickSort($low),
				array($pivot),
				$this->_quickSort($high)
				);
		}

		/**
		 * getLists
		 * @return array
		 */
		public function getLists() {
			if( !($this->_scanner_source instanceof CSyncanScanner) ) {
				//TODO Exception
			}

			if( !($this->_scanner_destination instanceof CSyncanScanner) ) {
				//TODO Exception
			}

			$sList = $this->_scanner_source->scan();
			$dList = $this->_scanner_destination->scan();

			$scount = count($sList);
			$dcount = count($dList);

			// -----------------------------------------------------------------

			if( $scount == 0 ) {
				return array(
					self::MKEY_SOURCE		=> array(),
					self::MKEY_DESTINATION	=> array(/*TODO*/),
					);
			}

			if( $dcount == 0 ) {
				return array(
					self::MKEY_SOURCE		=> array(/*TODO*/),
					self::MKEY_DESTINATION	=> array(),
					);
			}

			// sort lists ------------------------------------------------------

			$sList = $this->_quickSort($sList);
			$dList = $this->_quickSort($dList);

			// check lists -----------------------------------------------------

			$tos = array();
			$tod = array();

			$sIndex = 0;
			$dIndex = 0;

			while( ($scount>$sIndex) && ($dcount>$dIndex) ) {
				$sFileEntry = $sList[$sIndex];
				$dFileEntry = $dList[$dIndex];

				list($sFile, $sSize, $sTime, $ssum) = $sFileEntry;
				list($dFile, $dSize, $dTime, $dsum) = $dFileEntry;

				$statusFile = strcasecmp($sFile, $dFile);

				if( $statusFile === 0 ) {
					$sIndex++;
					$dIndex++;

					if( ($sSize != $dSize) || ($ssum != $dsum) ) {
						$tod[] = array(
							self::KEY_SCANNER	=> self::MKEY_SOURCE,
							self::KEY_STATUS		=> self::STATUS_FILE_CHANGE,
							self::KEY_FILEENTRY => $sFileEntry
							);

						$tos[] = array(
							self::KEY_SCANNER	=> self::MKEY_DESTINATION,
							self::KEY_STATUS		=> self::STATUS_FILE_CHANGE,
							self::KEY_FILEENTRY => $dFileEntry
							);
					}
				}
				else if( $statusFile < 0 ) {
					$sIndex++;

					$tod[] = array(
						self::KEY_SCANNER	=> self::MKEY_SOURCE,
						self::KEY_STATUS		=> self::STATUS_NOT_IN_DEST,
						self::KEY_FILEENTRY => $sFileEntry
						);
				}
				else if( $statusFile > 0 ) {
					$dIndex++;

					$tos[] = array(
						self::KEY_SCANNER	=> self::MKEY_DESTINATION,
						self::KEY_STATUS		=> self::STATUS_NOT_IN_SOUR,
						self::KEY_FILEENTRY => $dFileEntry
						);
				}
			}

			// -----------------------------------------------------------------

			if( $scount>$sIndex ) {
				for( $si=$sIndex; $si<$scount; $si++ ) {
					$sFileEntry = $sList[$si];

					$tod[] = array(
						self::KEY_SCANNER	=> self::MKEY_SOURCE,
						self::KEY_STATUS		=> self::STATUS_NOT_IN_DEST,
						self::KEY_FILEENTRY => $sFileEntry
						);
				}
			}

			if( $dcount>$dIndex ) {
				for( $di=$sIndex; $di<$scount; $di++ ) {
					$dFileEntry = $dList[$di];

					$tos[] = array(
						self::KEY_SCANNER	=> self::MKEY_DESTINATION,
						self::KEY_STATUS		=> self::STATUS_NOT_IN_SOUR,
						self::KEY_FILEENTRY => $dFileEntry
						);
				}
			}

			// -----------------------------------------------------------------

			return array(
				self::MKEY_SOURCE		=> $tos,
				self::MKEY_DESTINATION	=> $tod,
				);
		}
	}
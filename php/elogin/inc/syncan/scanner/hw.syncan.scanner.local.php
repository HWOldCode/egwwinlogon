<?php

	/**
	 * CSyncanScannerLocal
	 */
	class CSyncanScannerLocal extends CSyncanScanner {

		/**
		 * file list
		 * @var array
		 */
		protected $_filelist = array();

		/**
		 * scan
		 * @return array
		 */
		public function scan() {
			$this->_scan();
			return $this->_filelist;
		}

		/**
		 * _scan
		 * @param string $dir
		 */
		protected function _scan($dir='') {
			if( !($dirs=@dir($this->_uri . $dir)) ) {
				return;
			}

			while( ($entry=($dirs->read())) ) {
				if( ($entry == '.') || ($entry == '..') ) {
                	continue;
            	}

				if( (is_dir($this->_uri . $dir . $entry)) ) {
					$this->_scanDir($dir);
				}
				else {
					$this->_scanFile($dir . $entry);
				}
			}

			$dirs->close();
		}

		/**
		 * _scanFile
		 * @param string $file
		 */
		protected function _scanFile($file) {
			$info	= pathinfo($file);
			$tfile	= $this->_uri . $file;

			$this->_filelist[] = array(
				self::INDEX_FILE		=> $file,
				self::INDEX_SIZE		=> filesize($tfile),
                self::INDEX_TIME		=> filemtime($tfile),
				//self::INDEX_PATHINFO	=> $info,
				self::INDEX_CHECKSUM	=> $this->_checkSum($tfile)
				);
		}

		/**
		 * _scanDir
		 * @param string $dir
		 */
		protected function _scanDir($dir) {
			if( $this->_recursion ) {
				$this->_scan($dir);
			}
		}

		/**
		 * _checkSum
		 * @param string $file
		 * @return string
		 */
		protected function _checkSum($file) {
			return sha1_file($file);
		}
	}
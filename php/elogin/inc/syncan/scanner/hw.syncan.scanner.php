<?php

	/**
	 * CSyncanScanner
	 */
	class CSyncanScanner {

		// TYPES
		const TYPE_SOURCE			= 'source';
		const TYPE_DESTINATION		= 'destination';

		// ARRAY INDEXs
		const INDEX_FILE			= 0;
		const INDEX_SIZE			= 1;
		const INDEX_TIME			= 2;
		const INDEX_PATHINFO		= 3;
		const INDEX_CHECKSUM		= 4;

		/**
		 * type of scanner
		 * @var string
		 */
		protected $_type = "";

		/**
		 * recursion
		 * @var boolean
		 */
		protected $_recursion = false;

		/**
		 * uri to scanner destination
		 * @var string
		 */
		protected $_uri = '';

		/**
		 * __construct
		 * @param string $uri
		 * @param string $type
		 * @param boolean $recursion
		 */
		public function __construct($uri=null, $type=null, $recursion=null) {
			if( $uri !== null ) {
				$this->setUri($uri);
			}

			if( $type !== null ) {
				$this->setType($type);
			}

			if( $recursion !== null ) {
				$this->setRecursion($recursion);
			}
		}

		/**
		 * getType
		 * @return string
		 */
		public function getType() {
			return $this->_type;
		}

		/**
		 * setType
		 * @param string $type
		 */
		public function setType($type) {
			$this->_type = $type;
		}

		/**
		 * getRecursion
		 * @return boolean
		 */
		public function getRecursion() {
			return $this->_recursion;
		}

		/**
		 * setRecursion
		 * @param boolean $recursion
		 */
		public function setRecursion($recursion) {
			$this->_recursion = $recursion;
		}

		/**
		 * getUri
		 * @return string
		 */
		public function getUri() {
			return $this->_uri;
		}

		/**
		 * setUri
		 * @param string $uri
		 */
		public function setUri($uri) {
			$this->_uri = $uri;
		}

		/**
		 * scan
		 */
		public function scan() {
			return array();
		}
	}
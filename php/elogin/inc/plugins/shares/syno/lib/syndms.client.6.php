<?php

	/**
	 * Syndms
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package syno
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	/**
	 * SyndmsClient6
	 */
	class SyndmsClient6 extends SyndmsClientBase {

		// consts
		const VERSION_DSM_6	= '6';

		const URL_ENCRYPTION = 'encryption.cgi';

		/**
		 * encryption
		 * @var array|null
		 */
		protected $_encryption = null;

		/**
         * _createUrl
         * @param string $url
         * @return string
         */
        protected function _createUrl($url) {
			$url = sprintf($url, '');

			return parent::_createUrl($url);
		}

		/**
         * _initServices
         */
        public function _initServices() {
			parent::_initServices();
		}

		/**
		 * _loadEncryption
		 */
		public function _loadEncryption() {
			$data = $this->_queryByService('SYNO.API.Encryption', array(
				'format'	=> 'module',
				'api'		=> 'SYNO.API.Encryption',
				'method'	=> 'getinfo',
				'version'	=> '1'
				), true);

			if( $data !== null) {
				if( is_array($data) ) {
					$this->_encryption = $data;
				}
			}
		}

		/**
         * _initConnection
         * @return boolean
         */
        public function _initConnection() {
			$response = $this->_request(self::URL_INDEX);

			$this->_loadEncryption();

			if( $this->_encryption != null ) {
				$this->_initConnection = true;
				return true;
			}

			$this->_initConnection = false;
            return false;
        }
	}
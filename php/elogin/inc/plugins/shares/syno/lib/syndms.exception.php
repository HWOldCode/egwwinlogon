<?php

	/**
	 * SyndmsException
	 */
	class SyndmsException extends Exception {

		/**
		 * msg codes
		 * @var array
		 */
		static protected $_msg_codes = array(
			'100' => 'Unknown error',
            '101' => 'Invalid parameter',
            '102' => 'The requested API does not exist',
            '103' => 'The requested method does not exist',
            '104' => 'The requested version does not support the functionality',
            '105' => 'The logged in session does not have permission',
            '106' => 'Session timeout',
            '107' => 'Session interrupted by duplicate login',

			'400' => 'No such account or incorrect password',
            '401' => 'Guest account disabled',
            '402' => 'Account disabled',
            '403' => 'Wrong password',
            '404' => 'Permission denied',
			'405' => 'Invalid user and group does this file operation',
			'406' => 'Can’t get user/group information from the account server',
			'407' => 'Operation not permitted',
			'408' => 'No such file or directory',
			'409' => 'Non-supported file system',
			'410' => 'Failed to connect internet-based file system (ex: CIFS)',
			'411' => 'Read-only file system',
			'412' => 'Filename too long in the non-encrypted file system',
			'413' => 'Filename too long in the encrypted file system',
			'414' => 'File already exists',
			'415' => 'Disk quota exceeded',
			'416' => 'No space left on device',
			'417' => 'Input/output error',
			'418' => 'Illegal name or path',
			'419' => 'Illegal file name',
			'420' => 'Illegal file name on FAT file system',
			'421' => 'Device or resource busy',
			'599' => 'No such task of the file operation',

			'SYNO.API.Auth' => array(
				'400' => 'No such account or incorrect password',
				'401' => 'Account disabled',
				'402' => 'Permission denied',
				'403' => '2-step verification code required',
				'404' => 'Failed to authenticate 2-step verification code'
				),
			);

		/**
		 * servicename
		 * @var string
		 */
		protected $_servicename = '';

		/**
		 * method is called
		 * @var string
		 */
		protected $_method = '';

		/**
		 * request
		 * @var mixed
		 */
		protected $_request = null;

		/**
		 * response
		 * @var mixed
		 */
		protected $_response = null;

		/**
		 * __construct
		 * @param string $servicename
		 * @param string $method
		 * @param mixed $request
		 * @param mixed $response
		 * @param int $code
		 * @param Exception $previous
		 */
		public function __construct($servicename, $method, $request, $response, $code, $previous) {
			parent::__construct($this->_getMsg($code), $code, $previous);

			$this->_servicename = $servicename;
			$this->_method		= $method;
			$this->_request		= $request;
			$this->_response	= $response;
		}

		protected function _getMsg($code) {
			return '';
		}

		/**
		 * getServiceName
		 * @return string
		 */
		public function getServiceName() {
			return $this->_servicename;
		}

		/**
		 * getMethod
		 * @return string
		 */
		public function getMethod() {
			return $this->_method;
		}

		/**
		 * getRequest
		 * @return null|mixed
		 */
		public function getRequest() {
			return $this->_request;
		}

		/**
		 * getResponse
		 * @return null|mixed
		 */
		public function getResponse() {
			return $this->_response;
		}
	}
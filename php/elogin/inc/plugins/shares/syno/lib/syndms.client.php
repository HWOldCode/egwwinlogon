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
	 * incs
	 */
	require_once('syndms.client.base.php');
	require_once('syndms.client.5.php');
	require_once('syndms.client.6.php');

	/**
	 * SyndmsClient
	 */
	class SyndmsClient {

		// consts
		const VERSION_DSM_5	= '5';
		const VERSION_DSM_6	= '6';

		/**
		 * instance of client
		 * @var SyndmsClientBase
		 */
		protected $_instance = null;

		/**
         * __construct
         * @param string $ip
         * @param int $port
		 * @param string $protocol
         */
        public function __construct($ip, $port=5000, $protocol=null, $version=null) {
			if( ($version == null) || ($version == '') ) {
				$version = self::VERSION_DSM_5;
			}

			$class = 'SyndmsClient' . $version;

			if( class_exists($class) ) {
				$this->_instance = new $class($ip, $port, $protocol);
			}
		}

		/**
		 * __call
		 * @param string $method
		 * @param array $args
		 */
		public function __call($method, $args) {
			if( $this->_instance instanceof SyndmsClientBase ) {
				if( method_exists($this->_instance, $method) ) {
					return call_user_func_array(
						array($this->_instance, $method),
						$args
						);
				}
			}
		}
	}
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
	 * SyndmsClient5
	 */
	class SyndmsClient5 extends SyndmsClientBase {

		// consts
		const URL_PART = 'webman/';

		/**
         * _createUrl
         * @param string $url
         * @return string
         */
        protected function _createUrl($url) {
			$url = sprintf($url, self::URL_PART);

			return parent::_createUrl($url);
		}

		/**
         * _initConnection
         * @return boolean
         */
        public function _initConnection() {
            $response = $this->_request(self::URL_INDEX);

            $body = $response['body'];

            if( ($bpos = strpos($body, self::SYNO_SDS_SESSISON)) !== false ) {
                $bpos = strpos($body, "{", $bpos);
                $epos = strpos($body, ";", $bpos);

                $bspos = $bpos-1;

                $strsessiondata = substr(
                    $body,
                    $bspos,
                    $epos-$bspos
                    );

                $sessiondata =  json_decode($strsessiondata);

                if( $sessiondata instanceof stdClass ) {
                    $this->_sds_session = (array) $sessiondata;
					$this->_initConnection = true;
                    return true;
                }
            }

			$this->_initConnection = false;
            return false;
        }

		/**
         * _initServices
         */
        public function _initServices() {
			if( $this->_sds_session ) {
				parent::_initServices();
			}
		}
	}



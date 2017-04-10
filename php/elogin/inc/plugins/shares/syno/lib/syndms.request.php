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
     * SyndmsRequest
     * @author Stefan Werfling
     */
    class SyndmsRequest {

		// consts
		const DEBUG	= false;

		/**
		 * debug cache
		 * @var boolean
		 */
		static private $_debug_cache = false;

		/**
		 * cache logs
		 * @var array
		 */
		static private $_cache_logs = array();

		/**
         * curlRequest
         * @param string $query_string
         * @param array $postdata
         * @param string $content_type
         * @param array $custom_headers
		 * @param int $connection_timeout
         * @return array
         */
        static public function curlRequest($query_string, $postdata=null, $content_type=null, $custom_headers=null, $connection_timeout=3000) {
            $headers = (is_null($custom_headers)) ? array() : $custom_headers;
            $curl = curl_init();

			curl_setopt($curl, CURLOPT_URL, $query_string);
			curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
			curl_setopt($curl, CURLOPT_HEADER, 1);

            if( strpos($query_string, 'https://') !== false ) {
                curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, 0);
                curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, 0);
            }

            curl_setopt($curl, CURLOPT_VERBOSE, false);

            if( $postdata ) {
                if( is_array($postdata) ) {
                    $headers[] = "Content-type: application/x-www-form-urlencoded;charset=UTF-8";
                }
            }

            curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($curl, CURLOPT_ENCODING, "UTF-8");
			curl_setopt($curl, CURLOPT_FORBID_REUSE, true);
			curl_setopt($curl, CURLOPT_CONNECTTIMEOUT_MS, $connection_timeout);
			curl_setopt($curl, CURLOPT_TIMEOUT, 60*2);

			// -----------------------------------------------------------------

            if( $postdata ) {
                if( is_array($postdata) ) {
                    curl_setopt($curl, CURLOPT_POST, count($postdata));

                    $strpost = "";

                    foreach( $postdata as $key => $value ) {
                        if( $strpost != "" ) {
                            $strpost .= "&";
                        }

                        $strpost .= $key . "=" . str_replace('+', "%20", urlencode($value));
                    }

                    $postdata = $strpost;
                }

                curl_setopt($curl, CURLOPT_POSTFIELDS, $postdata);
            }

			// -----------------------------------------------------------------

            $response = curl_exec($curl);

			if( self::DEBUG || self::$_debug_cache ) {
				$info = curl_getinfo($curl);

				self::request_log('CURL-SENDE' . var_export($postdata, true), __LINE__);
				self::request_log('CURL-INFO' . var_export($info, true), __LINE__);
				self::request_log('CURL-RESPONSE' . var_export($response, true), __LINE__);
			}

			if( !curl_errno($curl) ) {
				$header_size = curl_getinfo($curl, CURLINFO_HEADER_SIZE);

				curl_close($curl);

				$header = substr($response, 0, $header_size);
				$body = substr($response, $header_size);

				return array(
					'header'	=> $header,
					'body'		=> $body
					);
			}
			else {
				$ex = new Exception(
					curl_error($curl),
					curl_errno($curl)
					);

				curl_close($curl);

				throw $ex;
			}

			return array();
        }

		/**
		 * request_log
		 * @param type $message
		 * @param type $line
		 */
		static public function request_log($message, $line) {
			if( self::$_debug_cache ) {
				self::cache_log($message, $line);
			}

			if( !self::DEBUG ) {
				return;
			}

			if( is_array($message) ) {
				$message = var_export($message, true);
			}

			$amessage = '';
			$amessage .= '--------------------------------------------------\r\n';
			$amessage .= 'Line: ' . $line . ' Message: ' . $message . "\r\n";

			$file = sys_get_temp_dir() . '/elogin_syndms.request.log';

			// -----------------------------------------------------------------

			if( file_exists($file) ) {
				$ftime = filectime($file);

				if( ($ftime !== false) && ($ftime <= strtotime("-2 day")) ) {
					unlink($file);
				}
			}

			// -----------------------------------------------------------------

			error_log($amessage, 3, $file);
		}

		/**
		 * cache_log
		 * @param string $message
		 * @param int $line
		 */
		static public function cache_log($message, $line) {
			if( !self::$_debug_cache ) {
				return;
			}

			if( is_array($message) ) {
				$message = var_export($message, true);
			}

			self::$_cache_logs[] = array($message, $line);
		}

		/**
		 * getCacheLogs
		 * @param boolean $clear
		 * @return array
		 */
		static public function getCacheLogs($clear=true) {
			$cache = self::$_cache_logs;

			if( $clear ) {
				self::$_cache_logs = array();
			}

			return $cache;
		}

		/**
		 * setCacheLogging
		 * @param boolean $logging
		 */
		static public function setCacheLogging($logging=false) {
			self::$_debug_cache = $logging;

			if( $logging ) {
				// is enable then set empty array
				self::$_cache_logs = array();
			}
		}
    }
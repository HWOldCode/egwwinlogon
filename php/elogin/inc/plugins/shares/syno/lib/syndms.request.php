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

		/**
		 * consts
		 */
		const DEBUG = false;

        /**
         * curlRequest
         *
         * @param string $query_string
         * @param array $postdata
         * @param string $content_type
         * @param array $custom_headers
         * @return array
         */
        public static function curlRequest($query_string, $postdata=null, $content_type=null, $custom_headers=null) {
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
			curl_setopt($curl, CURLOPT_CONNECTTIMEOUT_MS, 30000);
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

			if( self::DEBUG ) {
				$info = curl_getinfo($curl);

				self::request_error_log('CURL-SENDE' . var_export($postdata, true), __LINE__);
				self::request_error_log('CURL-INFO' . var_export($info, true), __LINE__);
				self::request_error_log('CURL-RESPONSE' . var_export($response, true), __LINE__);
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
		 * request_error_log
		 *
		 * @param type $message
		 * @param type $line
		 */
		static public function request_error_log($message, $line) {
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
    }
<?php

    /**
     * Description of syndms
     *
     * @author swe
     */
    class SyndmsRequest {

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

            curl_setopt($curl, CURLOPT_VERBOSE, true);
            curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);

            if( $postdata ) {
                if( is_array($postdata) ) {
                    curl_setopt($ch, CURLOPT_POST, count($postdata));

                    $strpost = "";

                    foreach( $postdata as $key => $value ) {
                        if( $strpost != "" ) {
                            $strpost .= "&";
                        }

                        $strpost .= $key . "=" . $value;
                    }

                    $postdata = $strpost;
                }

                curl_setopt($curl, CURLOPT_POSTFIELDS, $postdata);

            }

            $response = curl_exec($curl);
            $header_size = curl_getinfo($curl, CURLINFO_HEADER_SIZE);

            curl_close($curl);

            $header = substr($response, 0, $header_size);
            $body = substr($response, $header_size);

            return array(
                'header' => $header,
                'body' => $body
                );
        }
    }

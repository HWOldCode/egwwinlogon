<?php

     /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    /**
     * elogin_shareprovider_ui
     */
    class elogin_shareprovider_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'share_provider_list'       => true,
            'get_rows_shareprovider'    => true,
            'share_provider_edit'       => true,
            );

        /**
         * share_provider_list
         *
         * @param array $content
         */
        public function share_provider_list($content) {
            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_shareprovider_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_shareprovider_ui.get_rows_shareprovider',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'unid',
						'actions'       => array(),
                        'header_row'    => 'elogin.shareprovider_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new etemplate_new('elogin.shareprovider_list');
			$tpl->exec(
                'elogin.elogin_shareprovider_ui.share_provider_list',
                $content,
                array(),
                $readonlys,
                array(),
                0);
        }

        /**
         * get_rows_shareprovider
         *
         * @param type $query
         * @param type $rows
         * @param type $readonlys
         * @return type
         */
        public function get_rows_shareprovider(&$query, &$rows, &$readonlys) {
            egw_session::appsession('elogin_shareprovider_list', 'elogin', $query);

            return elogin_shareprovider_bo::get_rows($query, $rows, $readonlys);
        }

        /**
         * share_provider_edit
         *
         * @param array $content
         */
        public function share_provider_edit($content=null) {
            if( $content == null ) {
                $content = array();
            }

            $uid = ( isset($content['uid']) ? $content['uid'] : null);
			$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

            $preserv    = array();
            $option_sel = array();
            $readonlys  = array();

            if( $uid ) {
                $data = elogin_shareprovider_bo::read($uid);

            }

            $etemplate = new etemplate_new('elogin.share_provider.dialog');
            $etemplate->exec(
                    'elogin.elogin_shareprovider_ui.share_provider_edit',
                    array_merge($content, $preserv),
                    $option_sel,
                    $readonlys,
                    $preserv,
                    2);
        }
    }
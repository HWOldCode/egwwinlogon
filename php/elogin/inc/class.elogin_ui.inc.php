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
     * eline_ui
     */
    class elogin_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'index'             => true,
            'ajax_treelist'     => true,
            'ajax_cache'        => true,
            'ajax_machine_info' => true,
            'ajax_cmd'          => true,
            'ajax_logging'      => true,
            );

        public function index($content=array()) {
            
        }

        public function ajax_treelist($content=array()) {

        }

        /**
		 * ajax_cache
		 *
		 * @param array $content
		 * @return mixed
		 */
		public function ajax_cache($content=array()) {
            $elbo = new elogin_bo();
            $accountlist = $elbo->getEgroupwareAccounts();

            $type = @$GLOBALS['egw_info']['server']['sql_encryption_type'] ?
                strtolower($GLOBALS['egw_info']['server']['sql_encryption_type']) : 'md5';

            // return data for cache
            $cacheData = array(
                'egw_accounts' => $accountlist,
                'encryption_type' => $type
                );

            return egw_json_response::get()->data($cacheData);
        }

        /**
         * ajax_machine_info
         * @param array $content
         */
        public function ajax_machine_info($content=array()) {

            return egw_json_response::get()->data(array('status' => 'ok'));
        }

        /**
         * ajax_cmd
         *
         * @param array $content
         * @return mixed
         */
        public function ajax_cmd($content=array()) {

        }

        /**
         * ajax_logging
         * @param array $content
         */
        public function ajax_logging($content=array()) {

        }
    }
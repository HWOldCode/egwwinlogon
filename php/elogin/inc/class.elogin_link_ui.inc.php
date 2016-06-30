<?php

	/**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	/**
	 * elogin_link_ui
	 */
	class elogin_link_ui {

		/**
         * public methode
         * @var array
         */
        public $public_functions = array(
			'open'		=> true,
			'link_list'	=> true,
			'edit'		=> true
			);

		/**
		 * open
		 * @param array $content
		 */
		public function open($content=array()) {
			
		}
	}
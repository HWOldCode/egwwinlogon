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
     * elogin_machine_bo
     */
    class elogin_machine_bo {

        /**
         * id
         * @var string
         */
        protected $_id = null;

        /**
         * constructor
         * @param string $id
         */
        public function __construct($id) {
            $this->_id = $id;
        }
    }

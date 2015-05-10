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
     * elogin_usershares_mount_bo
     */
    class elogin_usershares_mount_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_usershares_mount';

        /**
         * id
         * @var string
         */
        protected $_id = null;

        /**
         * user share id
         * @var string
         */
        protected $_usershare_id = null;

        /**
         * machine id
         * @var string
         */
        protected $_machine_id = null;

        /**
         * account id
         * @var string
         */
        protected $_account_id = null;

        /**
         * share source (path to share)
         * @var string
         */
        protected $_share_source = '';

        /**
         * name of mount
         * @var string
         */
        protected $_mount_name = '';

        /**
         * constructor
         *
         * @param string $id
         */
        public function __construct($id=null) {
            if( $id != null ) {

            }
        }

        /**
         * getId
         *
         * @return string
         */
        public function getId() {
            return $this->_id;
        }

        /**
         * isGlobal
         *
         * @return boolean
         */
        public function isGlobal() {
            if( ($this->_machine_id == null) || ($this->_account_id == null) ) {
                return true;
            }

            return false;
        }

        /**
         * getUsershareId
         *
         * @return string
         */
        public function getUsershareId() {
            return $this->_usershare_id;
        }

        /**
         * setUsershareId
         *
         * @param string $id
         */
        public function setUsershareId($id) {
            $this->_usershare_id = $id;
        }


    }
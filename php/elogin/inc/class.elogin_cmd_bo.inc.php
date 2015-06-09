<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    /**
     * elogin_cmd_bo
     */
    class elogin_cmd_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_cmd';

        // types
        const TYPE_USER         = 'user';
        const TYPE_SERVICE      = 'service';

        const EVENT_LOGIN_PRE   = 'login_pre';
        const EVENT_LOGIN       = 'login';
        const EVENT_LOGIN_AFTER = 'login_after';

        /**
         * Reference to global db object
         *
         * @var egw_db
         */
        static protected $_db;

        /**
         * id
         * @var string
         */
        protected $_id = null;

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
         * command
         * @var string
         */
        protected $_command = '';

        /**
         * system
         * @var string
         */
        protected $_system = elogin_bo::SYSTEM_WIN;

        /**
         * order
         * @var int
         */
        protected $_order = 0;

        /**
         * type
         * @var string
         */
        protected $_type = elogin_cmd_bo::TYPE_USER;

        /**
         * event
         * @var string
         */
        protected $_event = elogin_cmd_bo::EVENT_LOGIN;

        /**
         * condition
         * @var string
         */
        protected $_condition = '';

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * constructor
         *
         * @param string $id
         */
        public function __construct($id=null) {
            if( $id != null ) {
                $data = self::read($id);
            }

            $this->_id = $id;
        }

        /**
         * setMachineId
         *
         * @param string $id
         */
        public function setMachineId($id) {
            $this->_machine_id = $id;
        }

        /**
         * getMachineId
         *
         * @return string
         */
        public function getMachineId() {
            return $this->_machine_id;
        }

        /**
         * setAccountId
         * @param string $id
         */
        public function setAccountId($id) {
            $this->_account_id = $id;
        }

        /**
         * getAccountId
         *
         * @return string
         */
        public function getAccountId() {
            return $this->_account_id;
        }

        /**
         * setCommand
         *
         * @param string $command
         */
        public function setCommand($command) {
            $this->_command = $command;
        }

        /**
         * getCommand
         *
         * @return string
         */
        public function getCommand() {
            return $this->_command;
        }

        /**
         * setSystem
         *
         * @param string $system
         */
        public function setSystem($system) {
            $this->_system = $system;
        }

        /**
         * getSystem
         *
         * @return string
         */
        public function getSystem() {
            return $this->_system;
        }

        /**
         * toArray
         * @return array
         */
        public function toArray() {
            return array(
                'id'            => $this->_id,
                'machine_id'    => $this->_machine_id,
                'account_id'    => $this->_account_id,
                'command'       => $this->_command,
                'system'        => $this->_system
                );
        }

        /**
         * toJson
         *
         * @return string
         */
        public function toJson() {
            return json_encode($this->toArray());
        }
    }

    /**
     * init
     */
    elogin_cmd_bo::init_static();
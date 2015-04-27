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

    require_once('lib/syndms.client.php');

    /**
     * elogin_syno_shareprovider_bo
     */
    class elogin_syno_shareprovider_bo extends elogin_shareprovider_bo {

        protected $_syno = null;

        /**
         * constructor
         * @param string $id
         */
        public function __construct($id=null) {}

        /**
         * _construct2
         */
        protected function _construct2() {
            if( $this->_account_server != null ) {
                $this->_syno = new SyndmsClient(
                    $this->_account_server,
                    $this->_account_port
                    );

                if( $this->_syno->login($this->_account_user, $this->_account_password) ) {
                    // TODO
                }
            }
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            if( $this->_syno ) {
                $shares = $this->_syno->getUserShares($this->_username);

                if( is_array($shares) ) {
                    return $shares;
                }
            }

            return array();
        }
    }
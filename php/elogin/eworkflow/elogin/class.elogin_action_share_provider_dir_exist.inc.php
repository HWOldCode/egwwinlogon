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
     * elogin_action_share_provider_dir_exist
     */
    class elogin_action_share_provider_dir_exist extends eworkflow_entry_bo implements eworkflow_ientry_bo, eworkflow_iparam_bo {
        
        // link action
		const LINK_YES      = 'yes';
		const LINK_NO       = 'no';
        
        /**
         * logger
         * @var CEcomanLogger
         */
        static protected $_logger = null;

        /**
         * param register
         *
         * @var eworkflow_param_register
         */
        static protected $_param_register = null;

        /**
         * type
         * @var string
         */
        protected $_type = CEcomanWorkflowEntry::TYPE_ACTION;
        
        /**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "share.png";
        }

        /**
		 * getEtemplate
		 *
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_shareprovider_dir_exist';
		}
        
        /**
         * acceptLinks
         * accept links
         *
         * @return array
         */
        public function acceptLinks() {
            return array(
				static::LINK_YES,
                static::LINK_NO
                );
        }
        
        /**
		 * getInfo
		 *
		 * @return array
		 */
		static public function getInfo() {
			return array(
				'title' => lang('Action EGW ELogin Share Provider dir exist'),
				'type' => self::TYPE_ACTION,
				'class' => static::_getClassName(self),
				'category' => array(
                    'ELogin',
					),
				);
		}
        
        /**
		 * uiEdit
		 *
		 * @param array $content
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
                
            }
            
            parent::uiEdit($content, $option_sel, $readonlys);
        }
        
        /**
		 * save
		 *
		 */
		public function save() {
            if( $this instanceof elogin_action_share_provider_dir_exist ) {
                
            }
            
            parent::save();
        }
        
        /**
         * execute
         *
         * @param type $params
         * @return type
         */
        public function execute($params) {
            if( !$this->_setStart($params) ) { return; };
            
            // get link for next action
            $this->_execNextEntryByLinkName($linkname, $params);
        }
        
        /**
         * getParameterRegister
         *
         * @return eworkflow_param_register
         */
        public function getParameterRegister() {
            if( static::$_param_register == null ) {
                static::$_param_register = new eworkflow_param_register();
            }

            $reg = static::$_param_register;

            return $reg;
        }
    }

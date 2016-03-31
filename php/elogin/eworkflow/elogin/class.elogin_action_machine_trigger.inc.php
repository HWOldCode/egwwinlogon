<?php

	/**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	/**
	 * elogin_action_machine_trigger
	 */
	class elogin_action_machine_trigger extends eworkflow_entry_bo implements eworkflow_ientry_bo, eworkflow_iparam_bo, eworkflow_ilink_bo {

		// link action
		const LINK_ACTION   = 'action';

		// params
		const PARAM_TRIGGER_TYPE	= 'trigger_type';
		const PARAM_RETURN_PARAMS	= 'return_params';

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
        protected $_type = CEcomanWorkflowEntry::TYPE_TRIGGER;

		/**
		 * trigger type
		 * @var string
		 */
		protected $_trigger_type = '';

		/**
		 * return params
		 * @var string
		 */
		protected $_return_params = '';

		/**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "machine_trigger.png";
        }

        /**
		 * getEtemplate
		 *
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_machine_trigger';
		}

		/**
         * acceptLinks
         * accept links
         *
         * @return array
         */
        public function acceptLinks() {
            return array(
				static::LINK_ACTION
                );
        }

		/**
         * hasLinkInput
         *
         * @return boolean
         */
        public function hasLinkInput() {
			return false;
		}

		/**
		 * getInfo
		 *
		 * @return array
		 */
		static public function getInfo() {
			return array(
				'title' => lang('Action EGW ELogin Machine Trigger'),
				'type' => self::TYPE_ACTION,
				'class' => static::_getClassName(self),
				'category' => array(
                    'ELogin',
					),
				);
		}

		/**
         * getTriggerType
         * @return string
         */
        public function getTriggerType() {
			return $this->_params->getVariableByParam(
				$this->_trigger_type, static::PARAM_TRIGGER_TYPE);
        }

		/**
		 * setTriggerType
		 * @param string $type
		 */
		public function setTriggerType($type) {
			$this->_trigger_type = $this->_params->saveVariableToParam(
				$type, static::PARAM_TRIGGER_TYPE);
		}

		/**
		 * getReturnParams
		 * @return string
		 */
		public function getReturnParams() {
			return $this->_params->getVariableByParam(
				$this->_return_params, static::PARAM_RETURN_PARAMS);
		}

		/**
		 * setReturnParams
		 * @param string $enable
		 */
		public function setReturnParams($enable) {
			$this->_return_params = $this->_params->saveVariableToParam(
				$enable, static::PARAM_RETURN_PARAMS);
		}

		/**
		 * uiEdit
		 *
		 * @param array $content
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
				$this->setTriggerType($content['trigger_type']);
				$this->setReturnParams($content['return_params']);
			}

			// -----------------------------------------------------------------

			$content['trigger_type'] = $this->getTriggerType();
			$option_sel['trigger_type'] = array(
				'request'	=> 'Request',
				'login'		=> 'Login',
				'logout'	=> 'Logout',
				'lock'		=> 'Lock',
				'unlock'	=> 'Unlock'
				);

			// -----------------------------------------------------------------

			$content['return_params'] = $this->getReturnParams();

			// -----------------------------------------------------------------

			if( $content['trigger_type'] == 'request' ) {
				$content['requesturl'] = 'http://localhost:8108/eworkflow/?trigger=' . $this->getId();
			}

			// -----------------------------------------------------------------

			parent::uiEdit($content, $option_sel, $readonlys);
		}

		/**
         * execute
         *
         * @param type $params
         * @return type
         */
        public function execute($params) {
            if( !$this->_setStart($params) ) { return; }

            // params merge
            // -----------------------------------------------------------------
			$ppo = new eworkflow_process_param_bo(
				$params,
				$this->getParamList(),
				$this->_entryParameter());

            $pro = $this->getParameterRegister();
            $pro->setProcessParam($ppo);

			$params = $ppo->getParams();

            // -----------------------------------------------------------------
			$linkname = static::LINK_ACTION;

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

		/**
		 * createByLinkSource
		 * @param type $sourceEntry
		 * @param type $sourceLink
		 */
		public function createByLinkSource($sourceEntry, $sourceLink=null) {}
	}
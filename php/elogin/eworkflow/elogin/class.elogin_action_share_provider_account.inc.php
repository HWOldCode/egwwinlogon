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
     * elogin_action_share_provider_account
     */
    class elogin_action_share_provider_account extends eworkflow_entry_bo implements eworkflow_ientry_bo, eworkflow_iparam_bo, eworkflow_ilink_style_bo {
		use eworkflow_parameter_register_base;

        // link action
		const LINK_ACTION   = 'action';
		const LINK_ERROR    = 'error';

        // Param
        const PARAM_SHAREPROVIDER_ACCOUNT    = 'shareprovider_account';

		/**
         * logger
         * @var CEcomanLogger
         */
        static protected $_logger = null;

        /**
         * type
         * @var string
         */
        protected $_type = CEcomanWorkflowEntry::TYPE_ACTION;

        /**
         * id of provider account
         * @var string
         */
        protected $_provider_account = '';

        /**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "provider.png";
        }

        /**
		 * getEtemplate
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_shareprovider_account';
		}

        /**
         * acceptLinks
         * accept links
         * @return array
         */
        public function acceptLinks() {
            return array(
				static::LINK_ACTION,
                static::LINK_ERROR
                );
        }

        /**
         * getLineStyle
         * @return array
         */
        public function getLineStyle() {
            return array(
                static::LINK_ERROR => array(
                    self::STYLE_LINE => self::LINESTYLE_DASH
                    )
            );
        }

        /**
		 * getInfo
		 * @return array
		 */
		static public function getInfo() {
			return array(
				'title' => lang('Action EGW ELogin Share Provider Account'),
				'type' => self::TYPE_ACTION,
				'class' => static::_getClassName(self),
				'category' => array(
                    'ELogin',
					),
				);
		}

        /**
         * getProviderAccountId
         * @return string
         */
        public function getProviderAccountId() {
			return $this->_params->getVariableByParam(
				$this->_provider_account, static::PARAM_SHAREPROVIDER_ACCOUNT);
        }

		/**
		 * setProviderAccountId
		 * @param string $id
		 */
		public function setProviderAccountId($id) {
			$this->_provider_account = $this->_params->saveVariableToParam(
				$id, static::PARAM_SHAREPROVIDER_ACCOUNT);
		}

        /**
         * getProvider
         * @return elogin_shareprovider_bo
         */
        public function getProvider() {
            $provideraccountid = $this->getProviderAccountId();

            if( $provideraccountid != '' ) {
                $provider = elogin_shareprovider_bo::i($provideraccountid);

                if( $provider instanceof elogin_shareprovider_bo ) {
                    return $provider;
                }
            }

            return null;
        }

        /**
		 * uiEdit
		 * @param array $content
		 * @param array $option_sel
		 * @param array $readonlys
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
                $this->setProviderAccountId($content['providers']);
            }

            $content['providers'] = $this->getProviderAccountId();
            $option_sel['providers'] = array();

            $providers = elogin_shareprovider_bo::getShareProviders();

            foreach( $providers as $provider ) {
                $option_sel['providers'][$provider->getId()] =
                    $provider->getProviderName() . " (" .
                    $provider->getAccountServer() . ")";
            }

			// -----------------------------------------------------------------

            parent::uiEdit($content, $option_sel, $readonlys);
        }

        /**
         * execute
         * @param array $params
         * @return array
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
            $linkname = static::LINK_ERROR;

            $provideraccountid = $this->getProviderAccountId();

            if( $provideraccountid != '' ) {
                $provider = $this->getProvider();

                if( $provider instanceof elogin_shareprovider_bo ) {
                    $pname = $provider->getProviderName() . " (" .
                        $provider->getAccountServer() . ")";

                    if( $provider->isLogin() ) {
                        $linkname = static::LINK_ACTION;
                        $this::$_logger->info('ShareProviderAccount is login: ' . $pname);
                    }
                    else {
                        $this::$_logger->severe('ShareProviderAccount isn`t login:' . $pname);
                    }
                }
                else {
                    $this::$_logger->severe('ShareProviderAccount isn`t found.');
                }
            }
            else {
                $this::$_logger->severe('ShareProviderAccount isn`t set.');
            }

            // -----------------------------------------------------------------

            // get link for next action
            $this->_execNextEntryByLinkName($linkname, $params);
        }

        /**
         * getParameterRegister
         * @return eworkflow_param_register
         */
        public function getParameterRegister() {
            return $this->_getParameterRegister();
        }
    }
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
     * elogin_action_share_provider_dir_permission_set
     */
    class elogin_action_share_provider_dir_permission_set extends eworkflow_entry_bo implements eworkflow_ientry_bo, eworkflow_iparam_bo {

        // link action
		const LINK_ACTION      = 'action';
		const LINK_ERROR       = 'error';

        // Param
        const PARAM_DPS_USERSHARE_ENTRY     = 'dps_usershare_entry';
        const PARAM_DPS_DIRNAME             = 'dps_dirname';
        const PARAM_DPS_USERNAME            = 'dps_username';
        const PARAM_DPS_READ                = 'dps_read';
        const PARAM_DPS_WRITE               = 'dps_write';

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
         * usershare entry id
         * @var string
         */
        protected $_usershare_entryid = "";

        /**
         * dirname
         * @var string
         */
        protected $_dirname = "";

        /**
         * username
         * @var string
         */
        protected $_username = "";

        /**
         *
         * @var string
         */
        protected $_dps_read = "";

        /**
         *
         * @var string
         */
        protected $_dps_write = "";

        /**
         * getEntryDefaultIcon
         * @return string
         */
        public function getEntryDefaultIcon() {
            return "permissionadd.png";
        }

        /**
		 * getEtemplate
		 *
		 * @return null|etemplate|string
		 */
		public function getEtemplate() {
			return 'entry.action_egw_elogin_shareprovider_dir_permission_set';
		}

        /**
         * acceptLinks
         * accept links
         *
         * @return array
         */
        public function acceptLinks() {
            return array(
				static::LINK_ACTION,
                static::LINK_ERROR
                );
        }

        /**
		 * getInfo
		 *
		 * @return array
		 */
		static public function getInfo() {
			return array(
				'title' => lang('Action EGW ELogin Share Provider dir permission set'),
				'type' => self::TYPE_ACTION,
				'class' => static::_getClassName(self),
				'category' => array(
                    'ELogin',
					),
				);
		}

        /**
         * getUserShareEntryid
         *
         * @return string
         */
        public function getUserShareEntryid() {
            $ue = $this->_params->getParam(static::PARAM_DPS_USERSHARE_ENTRY);

            if( $ue ) {
                $this->_usershare_entryid = $ue->getValue();
            }

            return $this->_usershare_entryid;
        }

        /**
         * getDirname
         *
         * @return string
         */
        public function getDirname() {
            $dn = $this->_params->getParam(static::PARAM_DPS_DIRNAME);

            if( $dn ) {
                $this->_dirname = $dn->getValue();
            }

            return $this->_dirname;
        }

        /**
         * getUsername
         *
         * @return string
         */
        public function getUsername() {
            $du = $this->_params->getParam(static::PARAM_DPS_USERNAME);

            if( $du ) {
                $this->_username = $du->getValue();
            }

            return $this->_username;
        }

        /**
		 * uiEdit
		 *
		 * @param array $content
		 */
		public function uiEdit(&$content, &$option_sel, &$readonlys) {
            if( isset($content['button']) && isset($content['button']['save']) ) {
                $this->_usershare_entryid = $content['usershare_entry'];
                $this->_dirname = $content['dirname'];
                $this->_username = $content['username'];
                $this->save();
            }

            $content['usershare_entry'] = $this->getUserShareEntryid();
            $option_sel['usershare_entry'] = array();

            $group = new CEcomanWorkflowEntryGroup($this->getGroupEntryId());
            $path = new eworkflow_entry_path_bo($group);

            // -----------------------------------------------------------------
            $fentry = $path->findEntryO($this,
                'elogin_action_share_provider_shares');

            while( $fentry instanceof CEcomanWorkflowEntry ) {
                $entry = eworkflow_entrys_bo::loadEntry($fentry->getId());

                if( $entry instanceof elogin_action_share_provider_shares ) {
                    $description = $entry->getDescription();

                    $option_sel['usershare_entry'][$entry->getId()] =
                        ($description == '' ? 'Title Empty' : $description);

                    $fentry = $path->findEntryO($entry,
                        'elogin_action_share_provider_shares');
                }
                else {
                    break;
                }
            }

            // -----------------------------------------------------------------
            $content['dirname'] = $this->getDirname();
            $content['options-dirname'] =
                eworkflow_ptextbox_etemplate_widget::createOptions(
                    $this->getGroupEntryId(),
                    $this->getId(),
                    array(
                        'onlyPlaceholder' => true,
                        'searchByTree' => true,
                        )
                    );

            $content['username'] =
                eworkflow_dialog_input_etemplate_widget::setSettingToValue(
					$this->getUsername(),
					array(
					));

            parent::uiEdit($content, $option_sel, $readonlys);
        }

        /**
		 * save
		 *
		 */
		public function save() {
            if( $this instanceof elogin_action_share_provider_dir_permission_set ) {
                $this->_saveVariableToParam(
                    $this->_usershare_entryid,
                    static::PARAM_DPS_USERSHARE_ENTRY
                    );

                $this->_saveVariableToParam(
                    $this->_dirname,
                    static::PARAM_DPS_DIRNAME
                    );

                $this->_saveVariableToParam(
                    $this->_username,
                    static::PARAM_DPS_USERNAME
                    );
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

            $linkname = self::LINK_ERROR;
            $dirname   = eworkflow_vfs_bo::cleanUtf8PathName(
                $pro->getParamValue(static::PARAM_DPS_DIRNAME));

            $username =
                eworkflow_dialog_input_etemplate_widget::getEWorkflowValueBy(
                    $this->getUsername(),
                    $pro
                    );

            $entryid = $this->getUserShareEntryid();
            $entry = eworkflow_entrys_bo::loadEntry($entryid);

            if( $entry instanceof elogin_action_share_provider_shares ) {
                $provider = $entry->getProvider();
                $sharename = $entry->getShareName();

                $this::$_logger->info('UserShare: ' . $sharename);

                if( $provider->addPermissionDir("/" . $sharename . '/', $dirname, $username, true, true) ) {
                    $linkname = self::LINK_ACTION;
                    $this::$_logger->info('Dir set permission in UserShare: ' . $dirname);
                }
                else {
                    $linkname = self::LINK_ERROR;
                    $this::$_logger->info('Dir can`t set permission in UserShare: ' . $dirname);
                }
            }

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

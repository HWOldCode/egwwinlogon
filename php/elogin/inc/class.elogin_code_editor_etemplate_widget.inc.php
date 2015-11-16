<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

    /**
     * elogin_code_editor_etemplate_widget
     */
    class elogin_code_editor_etemplate_widget extends etemplate_widget {

        /**
         * validate
         *
         * @param type $cname
         * @param array $expand
         * @param array $content
         * @param type $validated
         */
        public function validate($cname, array $expand, array $content, &$validated=array()) {
            $form_name = self::form_name($cname, $this->id, $expand);
            $value = self::get_array($content, $form_name);
			$valid =& self::get_array($validated, $form_name, true);
            $valid = $value;
        }
    }

    /**
     * register widget
     */
    etemplate_widget::registerWidget(
        'elogin_code_editor_etemplate_widget',
        array('elogin-code-editor')
        );
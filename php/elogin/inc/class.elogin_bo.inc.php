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
     * elogin_bo
     */
    class elogin_bo {

        const EVENT_WINDOWS_SESSION_CHANGE_LOGON    = 5;
        const EVENT_WINDOWS_SESSION_CHANGE_LOGOFF   = 6;

        const RECEIVER_WINDOWS_SERVICE_SYSTEM   = 0;
        const RECEIVER_WINDOWS_SERVICE_USER     = 1;
        const RECEIVER_WINDOWS_APP              = 2;
        
    }
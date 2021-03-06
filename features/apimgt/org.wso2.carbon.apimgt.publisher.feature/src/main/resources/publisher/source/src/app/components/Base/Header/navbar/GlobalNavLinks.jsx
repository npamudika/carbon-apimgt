/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import PropTypes from 'prop-types';
import Box from '@material-ui/core/Box';
import { FormattedMessage } from 'react-intl';
import AuthManager from 'AppData/AuthManager';
import ScopesIcon from '@material-ui/icons/VpnKey';
import { makeStyles } from '@material-ui/core/styles';

import GlobalNavLink from './GlobalNavLink';

const useStyles = makeStyles((theme) => ({
    scopeIcon: {
        color: theme.palette.background.paper,
    },
}));


/**
 *
 *
 * @param {*} props
 * @returns
 */
function GlobalNavLinks(props) {
    const publisherUser = !AuthManager.isNotPublisher();
    const classes = useStyles();
    const { selected } = props;
    return (
        <Box mt={10}>
            <GlobalNavLink
                to='/apis'
                type='apis'
                title='APIs'
                active={selected === 'apis'}
            >
                <FormattedMessage
                    id='Base.Header.navbar.GlobalNavBar.apis'
                    defaultMessage='APIs'
                />
            </GlobalNavLink>
            <GlobalNavLink
                to='/service-catalog'
                type='service-catalog'
                title='Services'
                active={selected === 'service-catalog'}
            >
                <FormattedMessage
                    id='Base.Header.navbar.GlobalNavBar.Service.Catalog'
                    defaultMessage='Service Catalog'
                />
            </GlobalNavLink>
            { publisherUser
                && (
                    <GlobalNavLink
                        to='/api-products'
                        type='api-product'
                        title='API Products'
                        active={selected === 'api-products'}
                    >
                        <FormattedMessage
                            id='Base.Header.navbar.GlobalNavBar.api.products'
                            defaultMessage='API Products'
                        />
                    </GlobalNavLink>
                )}
            <GlobalNavLink
                to='/scopes'
                type='scopes'
                title='Scopes'
                active={selected === 'scopes'}
                icon={<ScopesIcon className={classes.scopeIcon} style={{ fontSize: 25 }} />}
            >
                <FormattedMessage id='Base.Header.navbar.GlobalNavBar.scopes' defaultMessage='Scopes' />
            </GlobalNavLink>
        </Box>
    );
}
GlobalNavLinks.propTypes = {
    classes: PropTypes.shape({
        drawerStyles: PropTypes.string,
        list: PropTypes.string,
        listText: PropTypes.string,
    }).isRequired,
    theme: PropTypes.shape({
        palette: PropTypes.shape({
            getContrastText: PropTypes.func,
            background: PropTypes.shape({
                drawer: PropTypes.string,
                leftMenu: PropTypes.string,
            }),
        }),
    }).isRequired,
};

export default GlobalNavLinks;

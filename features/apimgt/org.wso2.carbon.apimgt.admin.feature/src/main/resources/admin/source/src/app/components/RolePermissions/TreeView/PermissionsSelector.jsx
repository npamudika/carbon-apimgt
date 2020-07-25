import React, { useState, useEffect } from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import CircularProgress from '@material-ui/core/CircularProgress';
import cloneDeep from 'lodash.clonedeep';

import Alert from 'AppComponents/Shared/Alert';
import PermissionTree from './PermissionTree';


/**
 *
 *
 * @export
 * @returns
 */
export default function PermissionsSelector(props) {
    const {
        appMappings, role, onSave,
    } = props;
    const [open, setOpen] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [localAppMappings, setLocalAppMappings] = useState({ ...appMappings });
    useEffect(() => {
        setLocalAppMappings(cloneDeep(appMappings));
    }, [appMappings]);

    const permissionCheckHandler = (event) => {
        const {
            name: scopeName, checked, role: selectedRole, app,
        } = event.target;
        const newAppMappings = { ...localAppMappings };
        newAppMappings[app] = newAppMappings[app].map(({ name, roles, ...rest }) => {
            if (name === scopeName) {
                if (checked) {
                    return { ...rest, name, roles: [...roles, selectedRole] };
                } else {
                    return { ...rest, name, roles: roles.filter((thisRole) => selectedRole !== thisRole) };
                }
            } else {
                return { name, roles, ...rest };
            }
        });
        setLocalAppMappings(newAppMappings);
    };

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        // TODO: Need to reset the mapping to last saved state ~tmkb
        setOpen(false);
        setLocalAppMappings(appMappings);
    };
    const handleSave = () => {
        setIsSaving(true);
        onSave(localAppMappings)
            .then(() => {
                Alert.info(
                    <span>
                        Update permissions for
                        {' '}
                        <b>{role}</b>
                        {' '}
                        successfully
                    </span>,
                );
                setOpen(false);
            })
            .catch((error) => {
                Alert.error('Something went wrong while updating the permissions');
                console.error(error);
            })
            .finally(() => setIsSaving(false));
    };
    return (
        <>
            <Button
                onClick={handleClickOpen}
                size='small'
                variant='outlined'
                color='primary'
            >
                Permissions
            </Button>
            <Dialog
                fullWidth
                maxWidth='md'
                open={open}
                disableBackdropClick={isSaving}
                onClose={handleClose}
                aria-labelledby='select-permissions-for-role'
            >
                <DialogTitle id='select-permissions-for-role'>
                    <Typography variant='h5' display='block' gutterBottom>
                        {role}
                        <Box display='inline' pl={1}>
                            <Typography variant='caption' gutterBottom>Select Permissions</Typography>
                        </Box>
                    </Typography>
                </DialogTitle>
                <DialogContent style={{ height: '90vh' }}>
                    <Box pl={5}>
                        <PermissionTree onCheck={permissionCheckHandler} role={role} appMappings={localAppMappings} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button
                        size='small'
                        variant='contained'
                        color='primary'
                        onClick={handleSave}
                        disabled={isSaving}
                    >
                        {isSaving && <CircularProgress size={16} />}
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}

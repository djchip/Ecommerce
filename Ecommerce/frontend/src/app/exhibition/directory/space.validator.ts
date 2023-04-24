import { AbstractControl } from '@angular/forms';

export function ValidateName(control: AbstractControl) {
    // debugger
    var value = control.value.trim().split(" ").pop()
    if (isNaN(Number(value))) {
        return { invalidName: true };
    }
    return null;
}

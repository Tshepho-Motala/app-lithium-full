export interface SpacerInterface {
  spacer: boolean
}
export interface ButtonInterface {
  /**
   * Text content of the button
   */
  text: string

  /**
   * The color key or hex of the button
   */
  color?: string | undefined

  /**
   * Click event handler of the button
   */
  onClick: () => void

  /**
   * Style of the button, true will force the button to look like a text link
   */
  flat?: boolean | undefined
}

export interface GenericDialogInterface {
  /**
   * Title of the dialog box
   */
  title: string | undefined

  /**
   * Text content of the dialog box
   */
  text: string | undefined

  /**
   * Definition of buttons / spacers for the actions
   */
  actionControls: (ButtonInterface | SpacerInterface)[] | undefined
}

export interface ConfirmDialogInterface {
  /**
   * Title of the dialog box
   */
  title: string | undefined

  /**
   * Text content of the dialog box
   */
  text: string | undefined

  /**
   * Description of the positive button
   */
  btnPositive: ButtonInterface | undefined

  /**
   * Description of the negative button
   */
  btnNegative: ButtonInterface | undefined
}
